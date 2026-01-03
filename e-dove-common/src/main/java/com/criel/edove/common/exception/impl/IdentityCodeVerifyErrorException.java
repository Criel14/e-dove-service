package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;

/**
 * 身份码校验异常：校验结果失败
 */
public class IdentityCodeVerifyErrorException extends BizException {

    public IdentityCodeVerifyErrorException() {
        super(ErrorCode.IDENTITY_CODE_VERIFY_ERROR);
    }

    public IdentityCodeVerifyErrorException(String extraMessage) {
        super(ErrorCode.IDENTITY_CODE_VERIFY_ERROR, extraMessage);
    }

}
