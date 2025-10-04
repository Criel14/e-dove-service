package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.enumeration.ErrorCode;

/**
 * 用户不存在
 */
public class UserNotFoundException  extends BaseException {

    public UserNotFoundException() {
        super(ErrorCode.USER_NOT_FOUND);
    }

    public UserNotFoundException(String extraMessage) {
        super(ErrorCode.USER_NOT_FOUND, extraMessage);
    }

}
