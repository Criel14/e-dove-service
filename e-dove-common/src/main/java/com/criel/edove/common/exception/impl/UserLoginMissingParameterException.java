package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户登录时：参数缺失
 */
public class UserLoginMissingParameterException extends BaseException {

    public UserLoginMissingParameterException() {
        super(ErrorCode.LOGIN_MISSING_PARAMETER);
    }

    public UserLoginMissingParameterException(String extraMessage) {
        super(ErrorCode.LOGIN_MISSING_PARAMETER, extraMessage);
    }

}
