package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户登录时：手机验证码错误
 */
public class UserSignInPhoneOtpException extends BaseException {

    public UserSignInPhoneOtpException() {
        super(ErrorCode.LOGIN_PHONE_OTP_ERROR);
    }

    public UserSignInPhoneOtpException(String extraMessage) {
        super(ErrorCode.LOGIN_PHONE_OTP_ERROR, extraMessage);
    }

}
