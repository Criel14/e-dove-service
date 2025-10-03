package com.criel.edove.common.util;

import com.criel.edove.common.exception.impl.IdentityCodeVerifyEmptyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Base36编码工具类
 */
public class Base36Util {

    private static final Logger LOGGER = LoggerFactory.getLogger(Base36Util.class);

    /**
     * 将字节数组 编码 为Base36字符串
     */
    public static String bytesToBase36(byte[] data) {
        // 把 data 视作大整数（BigInteger）
        java.math.BigInteger bi = new java.math.BigInteger(1, data);
        // 转换为36进制字符串：0–9 a–z(小写)
        return bi.toString(36);
    }

    /**
     * 将Base36字符串 解码 为字节数组
     */
    public static byte[] base36ToBytes(String base36) {
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
            throw new IdentityCodeVerifyEmptyException();
        }
    }

}
