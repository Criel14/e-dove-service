package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BizException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * jwt校验异常
 */
public class JWTException extends BizException {

    public JWTException() {
        super(ErrorCode.JWT_ERROR);
    }

    public JWTException(String extraMessage) {
        super(ErrorCode.JWT_ERROR, extraMessage);
    }

}
