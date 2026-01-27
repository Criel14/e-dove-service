package com.criel.edove.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.common.util.Base36Util;
import com.criel.edove.common.util.SipHashUtil;
import com.criel.edove.user.properties.BarcodeProperties;
import com.criel.edove.user.service.BarcodeService;
import com.criel.edove.user.vo.IdentityBarcodeBase64VO;
import com.criel.edove.user.vo.IdentityBarcodeVO;
import com.criel.edove.user.vo.VerifyBarcodeVO;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumMap;
import java.util.concurrent.TimeUnit;

/**
 * 条形码服务
 */
@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private final Logger LOGGER = LoggerFactory.getLogger(BarcodeServiceImpl.class);

    private final RedissonClient redissonClient;
    private final SnowflakeService snowflakeService;

    // 身份码前缀：ESU(Express Station User)
    private static final String codePrefix = "ESU";
    // 条形码编码参数
    EnumMap<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);

    private final BarcodeProperties barcodeProperties;

    @PostConstruct
    public void init() {
        // 编码
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        // 边距
        hints.put(EncodeHintType.MARGIN, 1);
    }

    /**
     * 生成用户【身份码】条形码原始数据
     */
    @Override
    public IdentityBarcodeVO generateUserBarcode() {
        // 当前用户手机号
        String phone = UserInfoContextHolder.getUserInfoContext().getPhone();
        // String code = getCodeV1(phone);
        String code = getCodeV2(phone);

        // 返回条形码原始数据
        return new IdentityBarcodeVO(code);
    }

    /**
     * 生成base64编码的用户【身份码】条形码图片（弃用）
     */
    @Override
    public IdentityBarcodeBase64VO generateUserBarcodeBase64() throws IOException, WriterException {
        // 当前用户手机号
        String phone = UserInfoContextHolder.getUserInfoContext().getPhone();
        // String code = getCodeV1(phone);
        String code = getCodeV2(phone);

        // 生成base64编码的条形码图片
        return new IdentityBarcodeBase64VO(generateBarcodeBase64(code));
    }

    /**
     * 生成身份码信息V1版本：手机号 + 时间戳 + 签名
     * @param phone 用户手机号
     */
    private String getCodeV1(String phone) {
        // 当前时间：精确到分钟
        long timestampMinute = System.currentTimeMillis() / 60000;

        // 明文部分
        String plainText = phone + timestampMinute;
        // SipHash2-4处理
        byte[] sipHash = SipHashUtil.sipHash24(barcodeProperties.getKey().getBytes(), plainText.getBytes());

        // 拼接签名和明文的byte数组
        byte[] data = new byte[sipHash.length + plainText.getBytes().length];
        System.arraycopy(sipHash, 0, data, 0, sipHash.length);
        System.arraycopy(plainText.getBytes(), 0, data, sipHash.length, plainText.getBytes().length);

        // Base36编码（由 0-9 a-z 组成的字符串）
        String base36 = Base36Util.bytesToBase36(data);

        // 转为大写，符合Code128
        base36 = base36.toUpperCase();
        return base36;
    }

    /**
     * 生成身份码信息V2版本：雪花ID + 校验位 + redis
     * @param phone 用户手机号
     */
    private String getCodeV2(String phone) {
        // 分布式锁
        String lockKey = RedisKeyConstant.USER_IDENTITY_CODE_LOCK + phone;
        RLock rLock = redissonClient.getLock(lockKey);
        boolean locked = false;
        try {
            locked = rLock.tryLock(10, TimeUnit.SECONDS);
            if (!locked) {
                throw new BizException(ErrorCode.IDENTITY_CODE_LOCK_ERROR);
            }

            // 先检查之前的身份码有没有过期
            String phoneToCodeKey = RedisKeyConstant.USER_IDENTITY_CODE_FLAG + phone;
            RBucket<String> flagBucket = redissonClient.getBucket(phoneToCodeKey);
            String originalCode = flagBucket.get();
            if (StrUtil.isNotEmpty(originalCode)) {
                // 不需要续期
                return originalCode;
            }

            // 如果过期了/不存在，则生成新的身份码
            long id = snowflakeService.nextId();
            char checkDigit = getCheckDigit(id);
            String newCode =  codePrefix + id + checkDigit;

            long ttl = 5;
            // 将【身份码 → 手机号】存入redis，5分钟的过期时间，驿站机器可以由此获取手机号信息
            String codeToPhoneKey = RedisKeyConstant.USER_IDENTITY_CODE_PREFIX + newCode;
            RBucket<String> codeBucket = redissonClient.getBucket(codeToPhoneKey);
            codeBucket.set(phone, Duration.ofMinutes(ttl));
            // 将【手机号 → 身份码】存入redis，5分钟过期时间，生成时可由此减少重复生成
            flagBucket.set(newCode, Duration.ofMinutes(ttl));

            return newCode;

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                rLock.unlock();
            }
        }
    }

    /**
     * 计算条形码的最后1位校验位：每一位相加 mod 36
     */
    private char getCheckDigit(long id) {
        long sum = 0;
        while (id > 0) {
            sum += id % 10;
            id /= 10;
        }
        long res = sum % 36;
        if (res < 10) {
            return (char)('0' + res);
        }
        return (char)('A' - 10 + res);
    }

    /**
     * 验证身份码
     *
     * @return 返回用户的手机号
     */
    @Override
    public VerifyBarcodeVO verifyIdentityBarcode(String code) {
        if (StrUtil.isEmpty(code)) {
            throw new BizException(ErrorCode.IDENTITY_CODE_VERIFY_EMPTY_ERROR);
        }
        String phone = verifyCodeV1(code);

        // 验证成功返回手机号
        return new VerifyBarcodeVO(phone);
    }

    /**
     * 验证身份码信息V1版本：手机号 + 时间戳 + 签名
     */
    private String verifyCodeV1(String code) {
        byte[] data = Base36Util.base36ToBytes(code.toLowerCase());

        // 截取出签名部分和明文部分
        // SipHash-2-4签名长度：算法生成长度为 8 的byte数组
        final int SIGN_LENGTH = 8;
        byte[] sipHashBytes = new byte[SIGN_LENGTH];
        byte[] plainTextBytes = new byte[data.length - SIGN_LENGTH];
        System.arraycopy(data, 0, sipHashBytes, 0, SIGN_LENGTH);
        System.arraycopy(data, SIGN_LENGTH, plainTextBytes, 0, plainTextBytes.length);

        // 截取出手机号和时间
        final int PHONE_LENGTH = 11; // 手机号码长度
        String phone = new String(plainTextBytes, 0, PHONE_LENGTH);
        String time = new String(plainTextBytes, PHONE_LENGTH, plainTextBytes.length - PHONE_LENGTH);
        long timestampMinute = Long.parseLong(time);

        // 校验是否过期
        if (Math.abs(System.currentTimeMillis() / 60000 - timestampMinute) > barcodeProperties.getTtl()) {
            throw new BizException(ErrorCode.IDENTITY_CODE_VERIFY_EXPIRED_ERROR);
        }

        // 校验签名
        String plainText = phone + time;
        byte[] sipHash = SipHashUtil.sipHash24(barcodeProperties.getKey().getBytes(), plainText.getBytes());
        if (!Arrays.equals(sipHash, sipHashBytes)) {
            throw new BizException(ErrorCode.IDENTITY_CODE_VERIFY_ERROR);
        }
        return phone;
    }

    /**
     * 验证身份码信息V2版本
     */
    private String verifyCodeV2(String code) {
        // 验证前缀
        if (!code.startsWith(codePrefix)) {
            throw new BizException(ErrorCode.IDENTITY_CODE_VERIFY_ERROR);
        }

        // 验证校验位
        String id = code.substring(3, code.length() - 1);
        char checkDigit = code.charAt(code.length() - 1);
        if (checkDigit != getCheckDigit(Long.parseLong(id))) {
            throw new BizException(ErrorCode.IDENTITY_CODE_VERIFY_ERROR);
        }

        // 从redis中获取用户的手机号
        String codeToPhoneKey = RedisKeyConstant.USER_IDENTITY_CODE_PREFIX + code;
        RBucket<String> codeBucket = redissonClient.getBucket(codeToPhoneKey);
        String phone = codeBucket.get();
        if (StrUtil.isEmpty(phone)) {
            throw new BizException(ErrorCode.IDENTITY_CODE_VERIFY_ERROR);
        }

        return phone;
    }

    /**
     * 根据内容生成Base64编码的条形码图片
     */
    private String generateBarcodeBase64(String content) throws WriterException, IOException {
        // 使用 MultiFormatWriter 生成 BitMatrix（黑白矩阵）
        BitMatrix bitMatrix = new MultiFormatWriter()
                .encode(content,
                        BarcodeFormat.CODE_128,
                        barcodeProperties.getWidth(),
                        barcodeProperties.getHeight(),
                        hints);

        // 转换为BufferedImage
        BufferedImage image = MatrixToImageWriter.toBufferedImage(bitMatrix);

        // 转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        byte[] imageBytes = baos.toByteArray();

        // 转换为Base64
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}
