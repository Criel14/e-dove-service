package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 验证码请求频率过快
 */
public class OtpRequestTooFrequentlyException extends BaseException {

    public OtpRequestTooFrequentlyException() {
        super(ErrorCode.OTP_REQUEST_TOO_FREQUENTLY);
    }

    public OtpRequestTooFrequentlyException(String extraMessage) {
        super(ErrorCode.OTP_REQUEST_TOO_FREQUENTLY, extraMessage);
    }

}
