package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 身份码校验异常
 */
public class IdentityCodeVerifyException extends BaseException {

    public IdentityCodeVerifyException() {
        super(ErrorCode.IDENTITY_CODE_VERIFY_ERROR);
    }

    public IdentityCodeVerifyException(String extraMessage) {
        super(ErrorCode.IDENTITY_CODE_VERIFY_ERROR, extraMessage);
    }

}
