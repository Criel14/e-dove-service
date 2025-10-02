package com.criel.edove.common.exception.impl;

import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.ErrorCode;

/**
 * 查询门店信息时，用户未绑定门店
 */
public class UserStoreNotBoundException extends BaseException {

    public UserStoreNotBoundException() {
        super(ErrorCode.USER_STORE_NOT_BOUND_ERROR);
    }

    public UserStoreNotBoundException(String extraMessage) {
        super(ErrorCode.USER_STORE_NOT_BOUND_ERROR, extraMessage);
    }

}
