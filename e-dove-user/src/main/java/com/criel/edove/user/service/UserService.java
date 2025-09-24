package com.criel.edove.user.service;

import com.criel.edove.common.result.Result;
import com.criel.edove.user.dto.LoginDTO;
import com.criel.edove.user.dto.RegisterDTO;
import com.criel.edove.user.entity.Permission;
import com.criel.edove.user.entity.Role;
import com.criel.edove.user.entity.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.criel.edove.user.enumeration.RoleEnum;
import com.criel.edove.user.vo.LoginVO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等 服务类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
public interface UserService extends IService<User> {

    List<Role> getRolesByUserId(Long userId);

    List<Permission> getPermissionsByUserId(Long userId);

    Result<LoginVO> login(LoginDTO loginDTO);

    Result<LoginVO> register(RegisterDTO registerDTO);

    User createUser(User user, RoleEnum roleEnum);

}
