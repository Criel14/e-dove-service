package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户注册时：参数缺失
 */
public class UserRegisterMissingParameterException extends BaseException {

    public UserRegisterMissingParameterException() {
        super(ErrorCode.REGISTER_MISSING_PARAMETER);
    }

    public UserRegisterMissingParameterException(String extraMessage) {
        super(ErrorCode.REGISTER_MISSING_PARAMETER, extraMessage);
    }

}
