package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;

/**
 * 注册请求过于频繁
 */
public class RegisterLockException extends BizException {

    public RegisterLockException() {
        super(ErrorCode.REGISTER_LOCK_ERROR);
    }

    public RegisterLockException(String extraMessage) {
        super(ErrorCode.REGISTER_LOCK_ERROR, extraMessage);
    }

}
