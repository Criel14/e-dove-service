package com.criel.edove.user.mapper;

import com.criel.edove.user.entity.UserRole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 * 用户与角色的关联关系表，支持一个用户拥有多个角色 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
