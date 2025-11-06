package com.criel.edove.store.service;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.store.dto.StoreBindDTO;
import com.criel.edove.store.dto.StoreDTO;
import com.criel.edove.store.entity.Store;
import com.baomidou.mybatisplus.extension.service.IService;
import com.criel.edove.store.vo.StoreVO;

/**
 * 门店信息服务
 *
 * @author Criel
 * @since 2025-10-02
 */
public interface StoreService extends IService<Store> {

    PageResult<StoreVO> page(int pageNum, int pageSize, String storeName);

    StoreVO getStoreInfoByUser();

    StoreVO createStore(StoreDTO storeDTO);

    void bindStore(StoreBindDTO storeBindDTO);

    StoreVO updateStore(StoreDTO storeDTO);

}
