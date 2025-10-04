package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 身份码校验异常：身份码已过期
 */
public class IdentityCodeVerifyExpiredException extends BaseException {

    public IdentityCodeVerifyExpiredException() {
        super(ErrorCode.IDENTITY_CODE_VERIFY_EXPIRED_ERROR);
    }

    public IdentityCodeVerifyExpiredException(String extraMessage) {
        super(ErrorCode.IDENTITY_CODE_VERIFY_EXPIRED_ERROR, extraMessage);
    }

}
