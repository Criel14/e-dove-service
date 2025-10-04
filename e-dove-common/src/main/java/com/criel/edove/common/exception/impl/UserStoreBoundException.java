package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BaseException;

/**
 * 绑定门店时，远程调用失败
 */
public class UserStoreBoundException extends BaseException {

    public UserStoreBoundException() {
        super(ErrorCode.USER_STORE_BOUND_ERROR);
    }

    public UserStoreBoundException(String extraMessage) {
        super(ErrorCode.USER_STORE_BOUND_ERROR, extraMessage);
    }

}
