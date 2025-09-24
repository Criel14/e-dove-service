package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户注册时：邮箱验证码错误
 */
public class UserRegisterEmailOtpException extends BaseException {

    public UserRegisterEmailOtpException() {
        super(ErrorCode.REGISTER_EMAIL_OTP_ERROR);
    }

    public UserRegisterEmailOtpException(String extraMessage) {
        super(ErrorCode.REGISTER_EMAIL_OTP_ERROR, extraMessage);
    }

}
