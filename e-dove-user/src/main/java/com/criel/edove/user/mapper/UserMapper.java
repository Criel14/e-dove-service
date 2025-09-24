package com.criel.edove.user.mapper;

import com.criel.edove.user.entity.Permission;
import com.criel.edove.user.entity.Role;
import com.criel.edove.user.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等 Mapper 接口
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    List<Role> getRolesByUserId(Long userId);

    List<Permission> getPermissionsByUserId(Long userId);

}
