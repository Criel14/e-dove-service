package com.criel.edove.auth.service.impl;

import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.auth.dto.OtpDTO;
import com.criel.edove.auth.dto.RegisterDTO;
import com.criel.edove.auth.dto.SignInDTO;
import com.criel.edove.auth.entity.Permission;
import com.criel.edove.auth.entity.Role;
import com.criel.edove.auth.entity.UserAuth;
import com.criel.edove.auth.entity.UserRole;
import com.criel.edove.auth.mapper.RoleMapper;
import com.criel.edove.auth.mapper.UserAuthMapper;
import com.criel.edove.auth.mapper.UserRoleMapper;
import com.criel.edove.auth.service.TokenService;
import com.criel.edove.auth.strategy.factory.LoginStrategyFactory;
import com.criel.edove.common.constant.LoginStrategyConstant;
import com.criel.edove.common.constant.RedisKeyConstant;
import com.criel.edove.common.constant.RegexConstant;
import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.enumeration.RoleEnum;
import com.criel.edove.common.exception.BaseException;
import com.criel.edove.common.exception.impl.*;
import com.criel.edove.auth.service.AuthService;
import com.criel.edove.auth.vo.SignInVO;
import com.criel.edove.common.service.SnowflakeService;
import com.criel.edove.feign.user.client.UserFeignClient;
import com.criel.edove.feign.user.dto.UserInfoDTO;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * 用户身份验证
 *
 * @author Criel
 * @since 2025-09-22
 */
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final Logger LOGGER = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final RedissonClient redissonClient;
    private final PasswordEncoder passwordEncoder;
    private final LoginStrategyFactory loginStrategyFactory;
    private final UserFeignClient userFeignClient;

    private final UserAuthMapper userAuthMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    private final SnowflakeService snowflakeService;
    private final TokenService tokenService;


    /**
     * 登录：策略模式实现多种登录方式，支持：
     * 1. 手机号 + 验证码（自动注册）
     * 2. 手机号 + 密码
     * 3. 邮箱 + 密码
     */
    @Override
    public SignInVO signIn(SignInDTO signInDTO) {
        // 根据参数的缺失情况选择登录策略
        String signInStrategy = checkSignInStrategy(signInDTO);
        UserAuth checkedUserAuth = loginStrategyFactory.getStrategy(signInStrategy).preSignIn(signInDTO);

        // 如果用户ID为null，则需要创建用户
        // 创建用户认证
        if (checkedUserAuth.getUserId() == null) {
            this.createUserAuthAndGrantRole(checkedUserAuth, RoleEnum.USER);
        }
        // 创建用户信息 TODO 需要分布式事务
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        userInfoDTO.setUserId(checkedUserAuth.getUserId());
        userInfoDTO.setPhone(checkedUserAuth.getPhone());
        userFeignClient.createUserInfo(userInfoDTO);

        // 生成2个token
        UserInfoContext userInfoContext = new UserInfoContext(
                checkedUserAuth.getUserId(),
                checkedUserAuth.getUsername(),
                checkedUserAuth.getPhone()
        );
        String accessToken = tokenService.createAccessToken(userInfoContext);
        String refreshToken = tokenService.createRefreshToken(userInfoContext);

        // 获取用户角色和权限列表
        List<Role> roles = userAuthMapper.getRolesByUserId(checkedUserAuth.getUserId());
        List<Permission> permissions = userAuthMapper.getPermissionsByUserId(checkedUserAuth.getUserId());

        return new SignInVO(
                accessToken,
                refreshToken,
                String.valueOf(checkedUserAuth.getUserId()),
                checkedUserAuth.getUsername(),
                roles.stream().map(Role::getRoleName).toList(),
                permissions.stream().map(Permission::getPermissionCode).toList()
        );
    }

    /**
     * 注册：手机号为必填项，其他的为选填
     */
    @Override
    public SignInVO register(RegisterDTO registerDTO) {
        // 验证参数是否缺失
        if (registerDTO.getPhone() == null || registerDTO.getPhoneOtp() == null) {
            throw new UserRegisterMissingParameterException();
        }

        // 验证手机号/邮箱/用户是否存在
        checkUserExists(registerDTO);

        // 验证码校验
        checkOtp(registerDTO.getPhone(), registerDTO.getPhoneOtp(), new UserRegisterPhoneOtpException());
        checkOtp(registerDTO.getEmail(), registerDTO.getEmailOtp(), new UserRegisterEmailOtpException());

        // 处理密码
        String hash = passwordEncoder.encode(registerDTO.getPassword());

        // 创建用户认证信息并指定角色
        UserAuth userAuth = new UserAuth();
        BeanUtils.copyProperties(registerDTO, userAuth); // 拷贝相同字段
        userAuth.setStatus(true);
        userAuth.setPassword(hash);
        userAuth.setStatus(true); // 设置默认用户状态
        // 默认是【普通用户】
        this.createUserAuthAndGrantRole(userAuth, RoleEnum.USER);

        // 创建新用户信息（远程调用）TODO 需要分布式事务
        UserInfoDTO userInfoDTO = new UserInfoDTO();
        BeanUtils.copyProperties(registerDTO, userInfoDTO);
        userInfoDTO.setUserId(userAuth.getUserId());
        userInfoDTO.setUsername(userAuth.getUsername());
        userFeignClient.createUserInfo(userInfoDTO);

        // 注册完自动完成登录
        return this.signIn(new SignInDTO(
                        registerDTO.getPhone(),
                        null,
                        null,
                        registerDTO.getPhoneOtp()
                )
        );
    }

    /**
     * 检查用户是否存在（合并多次数据库查询）
     */
    private void checkUserExists(RegisterDTO registerDTO) {
        LambdaQueryWrapper<UserAuth> wrapper = new LambdaQueryWrapper<>();

        // 使用or条件一次性查询
        wrapper.eq(UserAuth::getPhone, registerDTO.getPhone())
                .or(!StrUtil.isEmpty(registerDTO.getEmail()),
                        w -> w.eq(UserAuth::getEmail, registerDTO.getEmail()))
                .or(!StrUtil.isEmpty(registerDTO.getUsername()),
                        w -> w.eq(UserAuth::getUsername, registerDTO.getUsername()));

        List<UserAuth> existingUsers = userAuthMapper.selectList(wrapper);
        if (!existingUsers.isEmpty()) {
            for (UserAuth user : existingUsers) {
                if (registerDTO.getPhone().equals(user.getPhone())) {
                    throw new UserRegisterPhoneAlreadyExistsException();
                }
                if (!StrUtil.isEmpty(registerDTO.getEmail()) &&
                        registerDTO.getEmail().equals(user.getEmail())) {
                    throw new UserRegisterEmailAlreadyExistsException();
                }
                if (!StrUtil.isEmpty(registerDTO.getUsername()) &&
                        registerDTO.getUsername().equals(user.getUsername())) {
                    throw new UsernameAlreadyExistsException();
                }
            }
        }
    }

    /**
     * 验证码校验统一方法（手机/邮箱）
     */
    private void checkOtp(String phoneOrEmail, String otp, BaseException exception) {
        if (StrUtil.isNotEmpty(phoneOrEmail)) {
            String optKey = RedisKeyConstant.USER_OTP + phoneOrEmail;
            RBucket<String> otpBucket = redissonClient.getBucket(optKey);
            if (!otpBucket.isExists() || !StrUtil.equals(otp, otpBucket.get())) {
                throw exception;
            }
        }
    }

    /**
     * 获取验证码（手机号 / 邮箱）
     */
    @Override
    public void getOtp(OtpDTO otpDTO) {
        // 生成随机6位验证码
        String otp = RandomUtil.randomNumbers(6);

        String phoneOrEmail = otpDTO.getPhoneOrEmail();
        if (phoneOrEmail == null) {
            throw new OtpMissingParameterException();
        }
        // 判断是手机号还是邮箱
        if (phoneOrEmail.matches(RegexConstant.CHINA_PHONE_REGEX)) {
            // TODO 发送手机验证码
            LOGGER.info("发送手机验证码到：{}，验证码为：{}", phoneOrEmail, otp);
        } else if (phoneOrEmail.matches(RegexConstant.EMAIL_REGEX)) {
            // TODO 发送邮箱验证码
            LOGGER.info("发送邮箱验证码到：{}，验证码为：{}", phoneOrEmail, otp);
        } else {
            throw new OtpParameterException();
        }

        // 验证码存入redis
        String optKey = RedisKeyConstant.USER_OTP + phoneOrEmail;
        RBucket<String> otpBucket = redissonClient.getBucket(optKey);
        otpBucket.set(otp, Duration.ofMinutes(5));
    }

    /**
     * 根据登录i参数返回登录策略
     */
    private String checkSignInStrategy(SignInDTO signInDTO) {
        String phone = signInDTO.getPhone();
        String email = signInDTO.getEmail();
        String phoneOtp = signInDTO.getPhoneOtp();
        String password = signInDTO.getPassword();

        if (StrUtil.isNotEmpty(phone) && StrUtil.isNotEmpty(phoneOtp)) {
            return LoginStrategyConstant.PHONE_OTP_LOGIN_STRATEGY; // 手机号 + 验证码
        } else if (StrUtil.isNotEmpty(phone) && StrUtil.isNotEmpty(password)) {
            return LoginStrategyConstant.PHONE_PASSWORD_LOGIN_STRATEGY; // 手机号 + 密码
        } else if (StrUtil.isNotEmpty(email) && StrUtil.isNotEmpty(password)) {
            return LoginStrategyConstant.EMAIL_PASSWORD_LOGIN_STRATEGY; // 邮箱 + 密码
        }

        // 都匹配失败说明参数缺失
        throw new UserSignInMissingParameterException();
    }

    /**
     * 创新用户认证信息并指定角色，方法执行后userAuth会赋上userId
     *
     * @param userAuth 用户认证信息（不含用户ID）
     */
    private void createUserAuthAndGrantRole(UserAuth userAuth, RoleEnum roleEnum) {
        // 新用户ID
        Long userId = snowflakeService.nextId();
        userAuth.setUsername("edove" + userId);
        userAuth.setUserId(userId);
        userAuthMapper.insert(userAuth);

        // 授权
        // 根据枚举查询roleId
        LambdaQueryWrapper<Role> roleWrapper = new LambdaQueryWrapper<>();
        roleWrapper.eq(Role::getRoleName, roleEnum.getRoleName());
        Role role = roleMapper.selectOne(roleWrapper);
        // 创建用户角色关联
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(role.getRoleId());
        userRoleMapper.insert(userRole);
    }

}
