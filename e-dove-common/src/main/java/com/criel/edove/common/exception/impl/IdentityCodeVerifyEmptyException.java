package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 身份码校验异常：身份码为空
 */
public class IdentityCodeVerifyEmptyException extends BizException {

    public IdentityCodeVerifyEmptyException() {
        super(ErrorCode.IDENTITY_CODE_VERIFY_EMPTY_ERROR);
    }

    public IdentityCodeVerifyEmptyException(String extraMessage) {
        super(ErrorCode.IDENTITY_CODE_VERIFY_EMPTY_ERROR, extraMessage);
    }

}
