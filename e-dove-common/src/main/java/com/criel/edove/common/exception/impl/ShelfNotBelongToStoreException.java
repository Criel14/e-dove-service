package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BaseException;

/**
 * 更新货架时，货架不属于当前门店
 */
public class ShelfNotBelongToStoreException extends BaseException {

    public ShelfNotBelongToStoreException() {
        super(ErrorCode.SHELF_NOT_BELONG_TO_STORE);
    }

    public ShelfNotBelongToStoreException(String extraMessage) {
        super(ErrorCode.SHELF_NOT_BELONG_TO_STORE, extraMessage);
    }

}
