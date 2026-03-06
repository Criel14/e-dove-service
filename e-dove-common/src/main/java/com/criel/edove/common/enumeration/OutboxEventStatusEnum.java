package com.criel.edove.common.enumeration;

import lombok.Getter;

/**
 * 消息状态枚举
 */
@Getter
public enum OutboxEventStatusEnum {

    UNSENT (0, "未发送"),
    SENT (1, "已发送"),;

    private final Integer code;
    private final String desc;

    private OutboxEventStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

}
