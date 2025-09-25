package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 验证码请求参数缺失
 */
public class OtpMissingParameterException extends BaseException {

    public OtpMissingParameterException() {
        super(ErrorCode.OTP_MISSING_PARAMETER);
    }

    public OtpMissingParameterException(String extraMessage) {
        super(ErrorCode.OTP_MISSING_PARAMETER, extraMessage);
    }

}
