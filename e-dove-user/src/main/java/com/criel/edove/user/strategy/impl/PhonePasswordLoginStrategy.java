package com.criel.edove.user.strategy.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.exception.impl.UserLoginPasswordException;
import com.criel.edove.common.exception.impl.UserLoginPasswordNotFoundException;
import com.criel.edove.common.exception.impl.UserNotFoundException;
import com.criel.edove.common.result.Result;
import com.criel.edove.user.dto.LoginDTO;
import com.criel.edove.user.entity.User;
import com.criel.edove.user.mapper.UserMapper;
import com.criel.edove.user.service.AuthService;
import com.criel.edove.user.service.UserService;
import com.criel.edove.user.strategy.LoginStrategy;
import com.criel.edove.user.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * 用户登录策略：手机号 + 密码
 */
@Component
@RequiredArgsConstructor
public class PhonePasswordLoginStrategy implements LoginStrategy {

    private final UserMapper userMapper;
    private final AuthService authService;
    private final PasswordEncoder passwordEncoder;

    /**
     * @param loginDTO 调用时保证【手机号】和【密码】不为空
     */
    @Override
    public Result<LoginVO> login(LoginDTO loginDTO) {
        // 查找用户信息 并检查用户是否存在
        String phone = loginDTO.getPhone();
        LambdaQueryWrapper<User> userWrapper = new LambdaQueryWrapper<>();
        userWrapper.eq(User::getPhone, phone);
        User user = userMapper.selectOne(userWrapper);
        if (user == null) {
            throw new UserNotFoundException();
        }

        // 检查用户是否设置了密码
        if (StrUtil.isEmpty(user.getPassword())) {
            throw new UserLoginPasswordNotFoundException();
        }

        // 校验密码（比对【密码原文】和【存储的哈希值】）
        boolean isMatched = passwordEncoder.matches(loginDTO.getPassword(), user.getPassword());
        if (!isMatched) {
            throw new UserLoginPasswordException();
        }

        // 登录：生成2个token + 获取权限列表
        return Result.success(authService.login(user));
    }

}
