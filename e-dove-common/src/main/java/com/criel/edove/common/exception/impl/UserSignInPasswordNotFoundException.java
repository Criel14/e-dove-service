package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户使用密码登录，但用户未设置密码
 */
public class UserSignInPasswordNotFoundException extends BizException {

    public UserSignInPasswordNotFoundException() {
        super(ErrorCode.PASSWORD_NOT_FOUND);
    }

    public UserSignInPasswordNotFoundException(String extraMessage) {
        super(ErrorCode.PASSWORD_NOT_FOUND, extraMessage);
    }

}
