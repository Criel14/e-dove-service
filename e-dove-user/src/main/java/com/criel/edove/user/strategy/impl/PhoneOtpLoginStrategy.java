package com.criel.edove.user.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.exception.impl.UserLoginPhoneOtpException;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.user.dto.LoginDTO;
import com.criel.edove.user.entity.Role;
import com.criel.edove.user.entity.User;
import com.criel.edove.user.entity.UserRole;
import com.criel.edove.user.enumeration.RoleEnum;
import com.criel.edove.user.mapper.RoleMapper;
import com.criel.edove.user.mapper.UserMapper;
import com.criel.edove.user.mapper.UserRoleMapper;
import com.criel.edove.user.service.*;
import com.criel.edove.user.strategy.LoginStrategy;
import com.criel.edove.user.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

/**
 * 用户登录策略：手机号 + 验证码
 * 若用户不存在，则创建用户
 */
@Component
@RequiredArgsConstructor
public class PhoneOtpLoginStrategy implements LoginStrategy {

    private final RedissonClient redissonClient;

    private final SnowflakeService snowflakeService;
    private final AuthService authService;

    private final UserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    /**
     * @param loginDTO 调用时保证【手机号】和【验证码】不为空
     */
    @Override
    public Result<LoginVO> login(LoginDTO loginDTO) {
        // 查找用户信息
        String phone = loginDTO.getPhone();
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(userWrapper);

        // 验证码校验
        String otp = loginDTO.getPhoneOtp();
        String otpKey = RedisKeyConstant.USER_OTP + phone;
        RBucket<String> loginOtp = redissonClient.getBucket(otpKey);
        if (!loginOtp.isExists() || !StrUtil.equals(otp, loginOtp.get())) {
            throw new UserLoginPhoneOtpException();
        }

        // 验证码校验成功 且 用户不存在 则【创建用户 + 赋予用户角色】
        if (user == null) {
            user = new User();
            user.setPhone(phone);
            user = this.createUser(user, RoleEnum.USER);
        }

        // 登录：生成2个token + 获取权限列表
        return Result.success(authService.login(user));
    }

    /**
     * 创建新用户，并赋予用户角色
     * tip：该方法和 UserService 方法一模一样，但是直接调用那个方法会导致循环依赖，只能在这里复制一遍了
     *
     * @param user     新用户的基本信息（不含ID，手机号为必须，其他字敦可选）
     * @param roleEnum 新用户的角色
     */
    public User createUser(User user, RoleEnum roleEnum) {
        // 创建用户
        long userId = snowflakeService.nextId();
        user.setUserId(userId);
        if (StrUtil.isEmpty(user.getUsername())) {
            user.setUsername("dove" + userId); // 默认用户名
        }
        user.setStatus(true); // 初始用户状态为正常
        userMapper.insert(user);

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
