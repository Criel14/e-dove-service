package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 验证码请求参数有误：手机号 / 邮箱有误
 */
public class OtpParameterException extends BaseException {

    public OtpParameterException() {
        super(ErrorCode.OTP_PARAMETER_ERROR);
    }

    public OtpParameterException(String extraMessage) {
        super(ErrorCode.OTP_PARAMETER_ERROR, extraMessage);
    }

}
