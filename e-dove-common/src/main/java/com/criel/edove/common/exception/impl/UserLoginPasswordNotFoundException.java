package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户使用密码登录，但用户未设置密码
 */
public class UserLoginPasswordNotFoundException extends BaseException {

    public UserLoginPasswordNotFoundException() {
        super(ErrorCode.PASSWORD_NOT_FOUND);
    }

    public UserLoginPasswordNotFoundException(String extraMessage) {
        super(ErrorCode.PASSWORD_NOT_FOUND, extraMessage);
    }

}
