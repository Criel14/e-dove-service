package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户登录时：参数缺失
 */
public class UserSignInMissingParameterException extends BaseException {

    public UserSignInMissingParameterException() {
        super(ErrorCode.SIGN_IN_MISSING_PARAMETER);
    }

    public UserSignInMissingParameterException(String extraMessage) {
        super(ErrorCode.SIGN_IN_MISSING_PARAMETER, extraMessage);
    }

}
