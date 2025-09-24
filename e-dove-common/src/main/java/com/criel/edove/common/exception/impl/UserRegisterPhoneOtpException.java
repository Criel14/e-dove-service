package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 用户注册时：手机验证码错误
 */
public class UserRegisterPhoneOtpException extends BaseException {

    public UserRegisterPhoneOtpException() {
        super(ErrorCode.REGISTER_PHONE_OTP_ERROR);
    }

    public UserRegisterPhoneOtpException(String extraMessage) {
        super(ErrorCode.REGISTER_PHONE_OTP_ERROR, extraMessage);
    }

}
