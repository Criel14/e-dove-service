package com.criel.edove.common.enumeration;

import lombok.Getter;

/**
 * 包裹状态枚举
 */
@Getter
public enum ParcelStatusEnum {

    NEW_PARCEL (0, "新包裹/未入库"),
    IN_STORAGE (1, "已入库"),
    OUT_STORAGE (2, "已取出"),
    STALE(3, "滞留"),
    RETURN (4, "退回");

    private final Integer code;
    private final String desc;

    private ParcelStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    /**
     * Integer 转 Enum
     */
    public static ParcelStatusEnum fromCode(Integer code) {
        for (ParcelStatusEnum e : values()) {
            if (e.code.equals(code)) {
                return e;
            }
        }
        throw new IllegalArgumentException("未知状态: " + code);
    }

}
