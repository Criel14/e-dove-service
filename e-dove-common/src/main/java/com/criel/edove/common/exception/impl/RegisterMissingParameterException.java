package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户注册时：参数缺失
 */
public class RegisterMissingParameterException extends BaseException {

    public RegisterMissingParameterException() {
        super(ErrorCode.REGISTER_MISSING_PARAMETER);
    }

    public RegisterMissingParameterException(String extraMessage) {
        super(ErrorCode.REGISTER_MISSING_PARAMETER, extraMessage);
    }

}
