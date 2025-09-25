package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户名已存在
 */
public class UsernameAlreadyExistsException extends BaseException {

    public UsernameAlreadyExistsException() {
        super(ErrorCode.USERNAME_ALREADY_EXISTS);
    }

    public UsernameAlreadyExistsException(String extraMessage) {
        super(ErrorCode.USERNAME_ALREADY_EXISTS, extraMessage);
    }

}
