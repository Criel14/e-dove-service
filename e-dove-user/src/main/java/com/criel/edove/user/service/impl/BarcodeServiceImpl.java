package com.criel.edove.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.exception.impl.IdentityCodeVerifyEmptyException;
import com.criel.edove.common.exception.impl.IdentityCodeVerifyExpiredException;
import com.criel.edove.common.util.Base36Util;
import com.criel.edove.common.util.SipHashUtil;
import com.criel.edove.user.properties.BarcodeProperties;
import com.criel.edove.user.service.BarcodeService;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Base64;
import java.util.EnumMap;

/**
 * 条形码服务
 */
@Service
@RequiredArgsConstructor
public class BarcodeServiceImpl implements BarcodeService {

    private final Logger LOGGER = LoggerFactory.getLogger(BarcodeServiceImpl.class);

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
     * 生成base64编码的用户【身份码】条形码图片
     */
    @Override
    public IdentityBarcodeVO generateUserBarcodeBase64() throws IOException, WriterException {
        // 当前用户手机号
        String phone = UserInfoContextHolder.getUserInfoContext().getPhone();
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

        // 生成base64编码的条形码图片
        return new IdentityBarcodeVO(generateBarcodeBase64(base36));
    }

    /**
     * 验证身份码
     *
     * @return 返回用户的手机号
     */
    @Override
    public VerifyBarcodeVO verifyIdentityBarcode(String code) {
        if (StrUtil.isEmpty(code)) {
            throw new IdentityCodeVerifyEmptyException();
        }
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
            throw new IdentityCodeVerifyExpiredException();
        }

        // 校验签名
        String plainText = phone + time;
        byte[] sipHash = SipHashUtil.sipHash24(barcodeProperties.getKey().getBytes(), plainText.getBytes());
        if (!Arrays.equals(sipHash, sipHashBytes)) {
            throw new IdentityCodeVerifyEmptyException();
        }

        // 验证成功返回手机号
        return new VerifyBarcodeVO(phone);
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
