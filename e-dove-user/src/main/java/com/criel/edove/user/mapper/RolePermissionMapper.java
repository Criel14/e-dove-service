package com.criel.edove.user.mapper;

import com.criel.edove.user.entity.RolePermission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 角色与权限的关联关系表，实现灵活的权限控制 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Mapper
public interface RolePermissionMapper extends BaseMapper<RolePermission> {

}
