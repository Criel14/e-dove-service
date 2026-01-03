package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;

/**
 * 身份码生成异常：获取分布式锁失败，请求过于频繁
 */
public class IdentityCodeLockException extends BizException {

    public IdentityCodeLockException() {
        super(ErrorCode.IDENTITY_CODE_LOCK_ERROR);
    }

    public IdentityCodeLockException(String extraMessage) {
        super(ErrorCode.IDENTITY_CODE_LOCK_ERROR, extraMessage);
    }

}
