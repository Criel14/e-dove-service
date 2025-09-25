package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户注册时：邮箱已经被注册
 */
public class UserRegisterEmailAlreadyExistsException extends BaseException {

    public UserRegisterEmailAlreadyExistsException() {
        super(ErrorCode.EMAIL_ALREADY_EXISTS);
    }

    public UserRegisterEmailAlreadyExistsException(String extraMessage) {
        super(ErrorCode.EMAIL_ALREADY_EXISTS, extraMessage);
    }

}
