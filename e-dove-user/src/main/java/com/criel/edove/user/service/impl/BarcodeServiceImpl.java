package com.criel.edove.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.criel.edove.common.context.UserInfoContextHolder;
import com.criel.edove.common.exception.impl.HMACException;
import com.criel.edove.common.exception.impl.IdentityCodeVerifyException;
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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    // 时间格式：精确到分钟
    DateTimeFormatter MINUTE_FMT = DateTimeFormatter.ofPattern("yyyyMMddHHmm");

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
        String currentMinute = LocalDateTime.now().format(MINUTE_FMT);

        // 明文部分
        String plainText = phone + currentMinute;
        // HMAC-SHA256 签名，并截取前n字节
        byte[] hmacPart = hmacSha256(plainText);

        // 拼接签名和明文的byte数组
        byte[] data = new byte[hmacPart.length + plainText.getBytes().length];
        System.arraycopy(hmacPart, 0, data, 0, hmacPart.length);
        System.arraycopy(plainText.getBytes(), 0, data, hmacPart.length, plainText.getBytes().length);

        // Base36编码 结果形如：4f3owk1qw2snkav7k18tdf24s34py9aktjkp6qiykj07o
        String base36 = bytesToBase36(data);

        // 转为大写，符合Code128，并生成base64编码的条形码图片
        return new IdentityBarcodeVO(generateBarcodeBase64(base36.toUpperCase()));
    }

    /**
     * 验证身份码
     *
     * @return 返回用户的手机号
     */
    @Override
    public VerifyBarcodeVO verifyIdentityBarcode(String code) {
        if (StrUtil.isEmpty(code)) {
            throw new IdentityCodeVerifyException();
        }
        byte[] data = base36ToBytes(code.toLowerCase());

        // 截取出签名部分和明文部分
        byte[] hmacBytes = new byte[barcodeProperties.getSignLength()];
        byte[] plainTextBytes = new byte[data.length - barcodeProperties.getSignLength()];
        System.arraycopy(data, 0, hmacBytes, 0, barcodeProperties.getSignLength());
        System.arraycopy(data, barcodeProperties.getSignLength(), plainTextBytes, 0, plainTextBytes.length);

        // 截取出手机号和时间
        String phone = new String(plainTextBytes, 0, 11);
        String time = new String(plainTextBytes, 11, plainTextBytes.length - 11);
        LocalDateTime currentMinute = LocalDateTime.parse(time, MINUTE_FMT);

        // 校验是否过期
        if (currentMinute.isBefore(LocalDateTime.now().minusMinutes(barcodeProperties.getTtl()))) {
            throw new IdentityCodeVerifyException();
        }

        // 校验签名
        byte[] hmacPart = hmacSha256(new String(plainTextBytes));
        if (!Arrays.equals(hmacPart, hmacBytes)) {
            throw new IdentityCodeVerifyException();
        }

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

    /**
     * HMAC-SHA256 计算
     */
    private byte[] hmacSha256(String data) {
        try {
            Mac mac = Mac.getInstance(barcodeProperties.getAlgorithm());
            SecretKeySpec keySpec = new SecretKeySpec(barcodeProperties.getKey().getBytes(), barcodeProperties.getAlgorithm());
            mac.init(keySpec);
            byte[] hmac = mac.doFinal(data.getBytes());

            // 截取hmac的前n字节
            int n = barcodeProperties.getSignLength();
            byte[] hmacPart = new byte[n];
            System.arraycopy(hmac, 0, hmacPart, 0, n);
            return hmacPart;
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new HMACException();
        }
    }

    /**
     * 将字节数组 编码 为Base36字符串
     */
    private String bytesToBase36(byte[] data) {
        // 把 data 视作大整数（BigInteger）
        java.math.BigInteger bi = new java.math.BigInteger(1, data);
        // 转换为36进制字符串：0–9 a–z(小写)
        return bi.toString(36);
    }

    /**
     * 将Base36字符串 解码 为字节数组
     */
    private byte[] base36ToBytes(String base36) {
        try {
            // 将Base36字符串解析为BigInteger
            java.math.BigInteger bi = new java.math.BigInteger(base36, 36);
            // 转换为字节数组
            byte[] bytes = bi.toByteArray();
            // BigInteger.toByteArray() 的行为：对于正数：如果最高位字节 >= 0x80，会在开头添加0字节
            // 需要判断并移除这个额外的0字节
            if (bytes.length > 0 && bytes[0] == 0) {
                byte[] result = new byte[bytes.length - 1];
                System.arraycopy(bytes, 1, result, 0, result.length);
                return result;
            }
            return bytes;
        } catch (NumberFormatException e) {
            LOGGER.info("Base36解码失败，无效的Base36字符串:：{}", base36);
            throw new IdentityCodeVerifyException();
        }
    }

}
