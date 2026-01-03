package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户登录时：参数缺失
 */
public class UserSignInMissingParameterException extends BizException {

    public UserSignInMissingParameterException() {
        super(ErrorCode.SIGN_IN_MISSING_PARAMETER);
    }

    public UserSignInMissingParameterException(String extraMessage) {
        super(ErrorCode.SIGN_IN_MISSING_PARAMETER, extraMessage);
    }

}
