package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户登录时：手机验证码错误
 */
public class UserLoginPhoneOtpException extends BaseException {

    public UserLoginPhoneOtpException() {
        super(ErrorCode.LOGIN_PHONE_OTP_ERROR);
    }

    public UserLoginPhoneOtpException(String extraMessage) {
        super(ErrorCode.LOGIN_PHONE_OTP_ERROR, extraMessage);
    }

}
