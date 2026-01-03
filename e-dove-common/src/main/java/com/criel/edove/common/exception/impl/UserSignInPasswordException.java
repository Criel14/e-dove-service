package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户使用密码登录时：密码错误
 */
public class UserSignInPasswordException extends BizException {

    public UserSignInPasswordException() {
        super(ErrorCode.PASSWORD_ERROR);
    }

    public UserSignInPasswordException(String extraMessage) {
        super(ErrorCode.PASSWORD_ERROR, extraMessage);
    }

}
