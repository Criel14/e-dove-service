package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户注册时：手机号已经被注册
 */
public class RegisterPhoneAlreadyExistsException extends BaseException {

    public RegisterPhoneAlreadyExistsException() {
        super(ErrorCode.REGISTER_PHONE_ALREADY_EXISTS);
    }

    public RegisterPhoneAlreadyExistsException(String extraMessage) {
        super(ErrorCode.REGISTER_PHONE_ALREADY_EXISTS, extraMessage);
    }

}
