package com.criel.edove.parcel.util;

import java.util.Random;

/**
 * 生成不同快递公司的快递单号工具类
 */
public class TrackingNumberGenerator {

    private static final Random random = new Random();
    // {"顺丰", "圆通", "中通", "申通", "韵达", "京东", "德邦", "EMS", “极兔”};
    private static final String[] COMPANIES = {"SF", "YT", "ZTO", "STO", "YD", "JD", "DBL", "EMS", "JT"};

    /**
     * 生成固定长度的随机数字字符串
     */
    private static String randomDigits(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    /**
     * 生成一个随机快递公司的随机快递单号（格式就是快递公司名称缩写 + 12位随机数字，不按照真实的来了）
     */
    public static String generateOne() {
        String company = COMPANIES[random.nextInt(COMPANIES.length)];
        return company + randomDigits(12);
    }

}
