package com.criel.edove.user.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.exception.impl.*;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.user.constant.LoginStrategyConstant;
import com.criel.edove.user.dto.LoginDTO;
import com.criel.edove.user.dto.RegisterDTO;
import com.criel.edove.user.entity.Permission;
import com.criel.edove.user.entity.Role;
import com.criel.edove.user.entity.User;
import com.criel.edove.user.entity.UserRole;
import com.criel.edove.user.enumeration.RoleEnum;
import com.criel.edove.user.mapper.RoleMapper;
import com.criel.edove.user.mapper.UserMapper;
import com.criel.edove.user.mapper.UserRoleMapper;
import com.criel.edove.user.service.RoleService;
import com.criel.edove.user.service.UserRoleService;
import com.criel.edove.user.service.UserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.criel.edove.user.strategy.LoginStrategy;
import com.criel.edove.user.strategy.factory.LoginStrategyFactory;
import com.criel.edove.user.strategy.impl.EmailPasswordLoginStrategy;
import com.criel.edove.user.strategy.impl.PhoneOtpLoginStrategy;
import com.criel.edove.user.strategy.impl.PhonePasswordLoginStrategy;
import com.criel.edove.user.vo.LoginVO;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 存储系统所有用户的基本信息，包括用户、驿站工作人员、系统管理员等 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2025-09-23
 */
@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final SnowflakeService snowflakeService;

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;
    private final UserMapper userMapper;

    private final RedissonClient redissonClient;
    private final PasswordEncoder passwordEncoder;
    private final LoginStrategyFactory loginStrategyFactory;

    /**
     * 根据用户ID获取用户角色（1个用户可以有多个角色）
     */
    @Override
    public List<Role> getRolesByUserId(Long userId) {
        return userMapper.getRolesByUserId(userId);
    }

    /**
     * 根据用户ID获取权限列表
     */
    @Override
    public List<Permission> getPermissionsByUserId(Long userId) {
        return userMapper.getPermissionsByUserId(userId);
    }

    /**
     * 登录：策略模式实现多种登录方式，支持：
     * 1. 手机号 + 验证码
     * 2. 手机号 + 密码
     * 3. 邮箱 + 密码
     */
    @Override
    public Result<LoginVO> login(LoginDTO loginDTO) {
        String phone = loginDTO.getPhone();
        String email = loginDTO.getEmail();
        String phoneOtp = loginDTO.getPhoneOtp();
        String password = loginDTO.getPassword();

        // 根据参数的缺失情况选择登录策略
        if (phone != null && phoneOtp != null) {
            // 手机号 + 验证码
            return loginStrategyFactory.getStrategy(LoginStrategyConstant.PHONE_OTP_LOGIN_STRATEGY).login(loginDTO);
        } else if (phone != null && password != null) {
            // 手机号 + 密码
            return loginStrategyFactory.getStrategy(LoginStrategyConstant.PHONE_PASSWORD_LOGIN_STRATEGY).login(loginDTO);
        } else if (email != null && password != null) {
            // 邮箱 + 密码
            return loginStrategyFactory.getStrategy(LoginStrategyConstant.EMAIL_PASSWORD_LOGIN_STRATEGY).login(loginDTO);
        }

        // 都匹配失败说明参数缺失
        throw new UserLoginMissingParameterException();
    }

    /**
     * 注册：手机号为必填项，其他的为选填
     */
    @Override
    public Result<LoginVO> register(RegisterDTO registerDTO) {
        // 验证参数是否缺失
        if (registerDTO.getPhone() == null || registerDTO.getPhoneOtp() == null) {
            throw new UserRegisterMissingParameterException();
        }

        // 验证手机号是否已经被注册
        LambdaQueryWrapper<User> userPhoneWrapper = new LambdaQueryWrapper<>();
        userPhoneWrapper.eq(User::getPhone, registerDTO.getPhone());
        if (this.count(userPhoneWrapper) > 0) {
            throw new UserRegisterPhoneAlreadyExistsException();
        }

        // 验证邮箱是否已经被注册
        if (!StrUtil.isEmpty(registerDTO.getEmail())) {
            LambdaQueryWrapper<User> userEmailWrapper = new LambdaQueryWrapper<>();
            userEmailWrapper.eq(User::getEmail, registerDTO.getEmail());
            if (this.count(userEmailWrapper) > 0) {
                throw new UserRegisterEmailAlreadyExistsException();
            }
        }

        // 校验手机验证码
        String phoneOtp = registerDTO.getPhoneOtp();
        String phoneOtpKey = RedisKeyConstant.USER_LOGIN_OTP + registerDTO.getPhone();
        RBucket<String> loginOtp = redissonClient.getBucket(phoneOtpKey);
        if (!loginOtp.isExists() || !StrUtil.equals(phoneOtp, loginOtp.get())) {
            throw new UserRegisterPhoneOtpException();
        }

        // 校验邮箱验证码
        if (!StrUtil.isEmpty((registerDTO.getEmail())) && !StrUtil.isEmpty(registerDTO.getEmailOtp())) {
            String emailOtp = registerDTO.getEmailOtp();
            String emailOtpKey = RedisKeyConstant.USER_LOGIN_OTP + registerDTO.getEmail();
            RBucket<String> loginEmailOtp = redissonClient.getBucket(emailOtpKey);
            if (!loginEmailOtp.isExists() || !StrUtil.equals(emailOtp, loginEmailOtp.get()))
                throw new UserRegisterEmailOtpException();
        }

        // 处理密码
        String hash = passwordEncoder.encode(registerDTO.getPassword());

        // 创建新用户
        User user = new User();
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setPassword(hash);
        user.setAvatarUrl(registerDTO.getAvatarUrl());
        user = this.createUser(user, RoleEnum.USER);

        // 注册完自动完成登录
        return this.login(new LoginDTO(user.getPhone(), null, null, registerDTO.getPhoneOtp()));
    }

    /**
     * 创建新用户，并赋予用户角色
     *
     * @param user     新用户的基本信息（不含ID，手机号为必须，其他字敦可选）
     * @param roleEnum 新用户的角色
     */
    @Override
    public User createUser(User user, RoleEnum roleEnum) {
        // 创建用户
        long userId = snowflakeService.nextId();
        user.setUserId(userId);
        if (StrUtil.isEmpty(user.getUsername())) {
            user.setUsername("dove" + userId); // 默认用户名
        }
        user.setStatus(true); // 初始用户状态为正常
        this.save(user);

        // 赋予用户角色
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getRoleName, roleEnum.getRoleName());
        Role role = roleMapper.selectOne(roleWrapper);
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getRoleId());
        userRoleMapper.insert(userRole);

        return user;
    }
}
