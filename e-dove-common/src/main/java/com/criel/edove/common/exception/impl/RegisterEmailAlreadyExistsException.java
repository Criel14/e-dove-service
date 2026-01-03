package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户注册时：邮箱已经被注册
 */
public class RegisterEmailAlreadyExistsException extends BizException {

    public RegisterEmailAlreadyExistsException() {
        super(ErrorCode.REGISTER_EMAIL_ALREADY_EXISTS);
    }

    public RegisterEmailAlreadyExistsException(String extraMessage) {
        super(ErrorCode.REGISTER_EMAIL_ALREADY_EXISTS, extraMessage);
    }

}
