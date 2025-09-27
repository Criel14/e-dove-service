package com.criel.edove.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.auth.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定义系统中具体的操作权限点 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

}
