package com.criel.edove.user.controller;

import com.criel.edove.common.result.Result;
import com.criel.edove.user.dto.LoginDTO;
import com.criel.edove.user.dto.RegisterDTO;
import com.criel.edove.user.service.UserService;
import com.criel.edove.user.vo.LoginVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户信息操作 Controller
 *
 * @author Criel
 * @since 2025-09-23
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    /**
     * 登录接口
     * 若使用【手机号 + 验证码】登录，则会自动注册，但只会保存手机号信息，密码字段未设置（系统中，密码字段为可选）
     */
    @PostMapping("/login")
    public Result<LoginVO> login(@RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 注册接口
     * @return 注册完自动完成登录
     */
    @PostMapping("/register")
    public Result<LoginVO> register(@RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

}
