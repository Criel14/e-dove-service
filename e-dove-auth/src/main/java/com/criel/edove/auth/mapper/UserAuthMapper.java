package com.criel.edove.auth.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.criel.edove.auth.entity.Permission;
import com.criel.edove.auth.entity.Role;
import com.criel.edove.auth.entity.UserAuth;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 用户认证表，存储登录凭证和状态 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-27
 */
@Mapper
public interface UserAuthMapper extends BaseMapper<UserAuth> {

    /**
     * 根据用户id查询角色列表
     */
    List<Role> getRolesByUserId(Long userId);

    /**
     * 根据用户id查询权限列表
     */
    List<Permission> getPermissionsByUserId(Long userId);
}
