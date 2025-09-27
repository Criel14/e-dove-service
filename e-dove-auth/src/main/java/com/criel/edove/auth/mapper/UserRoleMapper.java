package com.criel.edove.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.auth.entity.UserRole;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 用户与角色的关联关系表，支持一个用户拥有多个角色 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Mapper
public interface UserRoleMapper extends BaseMapper<UserRole> {

}
