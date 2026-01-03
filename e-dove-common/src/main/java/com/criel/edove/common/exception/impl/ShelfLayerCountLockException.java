package com.criel.edove.common.exception.impl;

import com.criel.edove.common.enumeration.ErrorCode;
import com.criel.edove.common.exception.BizException;

/**
 * 更新货架层数量时获取分布式锁失败
 */
public class ShelfLayerCountLockException extends BizException {

    public ShelfLayerCountLockException() {
        super(ErrorCode.SHELF_LAYER_COUNT_LOCK_ERROR);
    }

    public ShelfLayerCountLockException(String extraMessage) {
        super(ErrorCode.SHELF_LAYER_COUNT_LOCK_ERROR, extraMessage);
    }

}
