package com.edove.criel.common.exception.impl;

import com.edove.criel.common.exception.BaseException;
import com.edove.criel.common.exception.ErrorCode;

public class SystemException extends BaseException {

    public SystemException() {
        super(ErrorCode.SYSTEM_ERROR);
    }

    public SystemException(String extraMessage) {
        super(ErrorCode.SYSTEM_ERROR, extraMessage);
    }
}
