package com.criel.edove.parcel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.parcel.entity.Parcel;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 包裹信息表 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-10-02
 */
@Mapper
public interface ParcelMapper extends BaseMapper<Parcel> {

}
