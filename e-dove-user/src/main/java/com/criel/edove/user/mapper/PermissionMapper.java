package com.criel.edove.user.mapper;

import com.criel.edove.user.entity.Permission;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定义系统中具体的操作权限点 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
