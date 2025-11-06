package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BaseException;

/**
 * 无法通过storeId找到门店 / 门店不存在
 */
public class StoreNotFoundException extends BaseException {

    public StoreNotFoundException() {
        super(ErrorCode.STORE_NOT_FOUND_ERROR);
    }

    public StoreNotFoundException(String extraMessage) {
        super(ErrorCode.STORE_NOT_FOUND_ERROR, extraMessage);
    }

}
