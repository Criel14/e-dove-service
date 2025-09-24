package com.criel.edove.user.mapper;

import com.criel.edove.user.entity.Role;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 * 定义系统中的角色类型 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {

}
