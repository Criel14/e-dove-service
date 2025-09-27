package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户使用密码登录时：密码错误
 */
public class UserSignInPasswordException extends BaseException {

    public UserSignInPasswordException() {
        super(ErrorCode.PASSWORD_ERROR);
    }

    public UserSignInPasswordException(String extraMessage) {
        super(ErrorCode.PASSWORD_ERROR, extraMessage);
    }

}
