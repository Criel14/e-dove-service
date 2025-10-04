package com.criel.edove.common.enumeration;

import lombok.Getter;

/**
 * 门店状态枚举
 */
@Getter
public enum StoreStatusEnum {

    // 营业
    OPEN(1, "营业"),
    // 休息
    CLOSED(2, "休息"),
    // 注销
    STOPPED(3, "注销");


    private final int code;
    private final String desc;

    StoreStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
