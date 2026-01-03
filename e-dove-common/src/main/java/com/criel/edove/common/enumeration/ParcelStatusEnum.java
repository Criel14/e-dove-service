package com.criel.edove.common.enumeration;

import lombok.Getter;

/**
 * 包裹状态枚举
 */
@Getter
public enum ParcelStatusEnum {

    NEW_PARCEL (0, "新包裹"),
    IN_STORAGE (1, "已入库"),
    OUT_STORAGE (2, "已取出"),
    LOST (3, "滞留"),
    RETURN (4, "退回");

    private final Integer code;
    private final String desc;

    private ParcelStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
