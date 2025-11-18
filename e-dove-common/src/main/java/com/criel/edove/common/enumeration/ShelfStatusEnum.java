package com.criel.edove.common.enumeration;

import lombok.Getter;

/**
 * 货架状态枚举
 */
@Getter
public enum ShelfStatusEnum {

    // 正常启用
    ENABLE(1, "正常启用"),
    // 停用/维护
    DISABLE(0, "停用或维护"),;

    private final int code;
    private final String desc;

    ShelfStatusEnum(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
