package com.criel.edove.common.util;

/**
 * 工具类：计算SipHash：相比 HMAC-SHA256 或 HMAC-SHA1，耗时基本是其 1/10
 */
public class SipHashUtil {

    /**
     * 计算SipHash-2-4并返回8字节的byte数组
     *
     * @return 8字节的byte数组
     */
    public static byte[] sipHash24(byte[] key, byte[] data) {
        // 处理密钥：填充或截断为16字节
        byte[] processedKey = processKey(key);

        long k0 = bytesToLong(processedKey, 0);
        long k1 = bytesToLong(processedKey, 8);

        // 这些数字是 SipHash 算法的标准组成部分，不可修改
        long v0 = 0x736f6d6570736575L ^ k0;
        long v1 = 0x646f72616e646f6dL ^ k1;
        long v2 = 0x6c7967656e657261L ^ k0;
        long v3 = 0x7465646279746573L ^ k1;

        // 处理完整块
        int lastFullBlock = data.length & ~7; // data.length / 8 * 8
        for (int i = 0; i < lastFullBlock; i += 8) {
            long m = bytesToLong(data, i);
            v3 ^= m;
            sipRound(v0, v1, v2, v3, 2);
            v0 ^= m;
        }

        // 处理最后的不完整块
        long m = ((long) data.length) << 56;
        int remaining = data.length - lastFullBlock;
        for (int i = 0; i < remaining; i++) {
            m |= ((long) (data[lastFullBlock + i] & 0xFF)) << (8 * i);
        }
        v3 ^= m;
        sipRound(v0, v1, v2, v3, 2);
        v0 ^= m;

        // 最终处理
        v2 ^= 0xFF;
        sipRound(v0, v1, v2, v3, 4);

        long result = v0 ^ v1 ^ v2 ^ v3;
        return longToBytes(result);
    }

    /**
     * 处理密钥：不足16字节则填充0，超过16字节则截取前16字节
     */
    private static byte[] processKey(byte[] key) {
        if (key.length == 16) {
            return key;
        }

        byte[] processedKey = new byte[16];
        int copyLength = Math.min(key.length, 16);
        System.arraycopy(key, 0, processedKey, 0, copyLength);

        // 剩余部分自动为0（Java数组初始化默认值为0）
        return processedKey;
    }

    /**
     * SipHash轮函数
     */
    private static void sipRound(long v0, long v1, long v2, long v3, int rounds) {
        for (int j = 0; j < rounds; j++) {
            v0 += v1;
            v2 += v3;
            v1 = Long.rotateLeft(v1, 13);
            v3 = Long.rotateLeft(v3, 16);
            v1 ^= v0;
            v3 ^= v2;
            v0 = Long.rotateLeft(v0, 32);
            v2 += v1;
            v0 += v3;
            v1 = Long.rotateLeft(v1, 17);
            v3 = Long.rotateLeft(v3, 21);
            v1 ^= v2;
            v3 ^= v0;
            v2 = Long.rotateLeft(v2, 32);
        }
    }

    /**
     * 从字节数组读取long（小端序）
     */
    private static long bytesToLong(byte[] bytes, int offset) {
        long result = 0;
        int end = Math.min(offset + 8, bytes.length);
        for (int i = offset; i < end; i++) {
            result |= (long) (bytes[i] & 0xFF) << (8 * (i - offset));
        }
        return result;
    }

    /**
     * 将long转换为8字节数组（小端序）
     */
    private static byte[] longToBytes(long value) {
        byte[] result = new byte[8];
        for (int i = 0; i < 8; i++) {
            result[i] = (byte) (value >>> (8 * i));
        }
        return result;
    }

    /**
     * 保持向后兼容的方法，返回long类型
     */
    public static long rawSipHash24(byte[] key, byte[] data) {
        byte[] hashBytes = sipHash24(key, data);
        return bytesToLong(hashBytes, 0);
    }
}