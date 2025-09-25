package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户注册时：手机号已经被注册
 */
public class UserRegisterPhoneAlreadyExistsException extends BaseException {

    public UserRegisterPhoneAlreadyExistsException() {
        super(ErrorCode.PHONE_ALREADY_EXISTS);
    }

    public UserRegisterPhoneAlreadyExistsException(String extraMessage) {
        super(ErrorCode.PHONE_ALREADY_EXISTS, extraMessage);
    }

}
