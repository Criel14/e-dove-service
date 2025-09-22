package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

public class JWTException extends BaseException {

    public JWTException() {
        super(ErrorCode.JWT_ERROR);
    }

    public JWTException(String extraMessage) {
        super(ErrorCode.JWT_ERROR, extraMessage);
    }

}
