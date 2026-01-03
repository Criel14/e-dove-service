package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户名已存在
 */
public class RegisterUsernameAlreadyExistsException extends BizException {

    public RegisterUsernameAlreadyExistsException() {
        super(ErrorCode.REGISTER_USERNAME_ALREADY_EXISTS);
    }

    public RegisterUsernameAlreadyExistsException(String extraMessage) {
        super(ErrorCode.REGISTER_USERNAME_ALREADY_EXISTS, extraMessage);
    }

}
