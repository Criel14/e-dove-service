package com.criel.edove.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.auth.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定义系统中的角色类型 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}
