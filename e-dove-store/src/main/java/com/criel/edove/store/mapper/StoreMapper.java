package com.criel.edove.store.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.criel.edove.store.entity.Store;
import com.criel.edove.store.vo.StoreVO;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 门店信息表 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Mapper
public interface StoreMapper extends BaseMapper<Store> {

    IPage<StoreVO> selectPageByStoreName(IPage<?> page, String storeName);

}
