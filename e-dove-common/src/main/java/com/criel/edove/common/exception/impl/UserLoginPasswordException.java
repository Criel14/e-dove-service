package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户使用密码登录时：密码错误
 */
public class UserLoginPasswordException extends BaseException {

    public UserLoginPasswordException() {
        super(ErrorCode.PASSWORD_ERROR);
    }

    public UserLoginPasswordException(String extraMessage) {
        super(ErrorCode.PASSWORD_ERROR, extraMessage);
    }

}
