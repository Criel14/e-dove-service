package com.criel.edove.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.user.entity.UserAddress;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 存储用户的收货地址信息，支持设置默认地址 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

}
