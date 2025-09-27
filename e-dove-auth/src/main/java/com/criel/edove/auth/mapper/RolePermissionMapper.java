package com.criel.edove.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.auth.entity.RolePermission;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色与权限的关联关系表，实现灵活的权限控制 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}
