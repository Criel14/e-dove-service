package com.criel.edove.auth.controller;

import com.criel.edove.auth.dto.SignInDTO;
import com.criel.edove.auth.dto.OtpDTO;
import com.criel.edove.auth.dto.RegisterDTO;
import com.criel.edove.auth.dto.UpdateUserAuthDTO;
import com.criel.edove.auth.service.TokenService;
import com.criel.edove.auth.vo.SignInVO;
import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.dto.PingDTO;
import com.criel.edove.common.result.Result;
import com.criel.edove.auth.service.AuthService;
import com.criel.edove.auth.vo.TokenRefreshVO;
import com.criel.edove.common.vo.PingVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 权限认证 Controller
 *
 * @author Criel
 * @since 2025-09-22
 */
// TODO 用 AOP + 自定义注解 实现接口粒度的权限认证
@RequiredArgsConstructor
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenService tokenService;

    /**
     * 连接测试
     */
    @GetMapping("/ping")
    public Result<PingVO> ping(@RequestParam PingDTO pingDTO) {
        return Result.success(new PingVO(pingDTO.getMessage()));
    }

    /**
     * 校验 access token，成功则返回用户信息；
     */
    @GetMapping("/validate")
    public Result<UserInfoContext> validateAccessToken(@RequestParam String accessToken) {
        return Result.success(tokenService.validateAccessToken(accessToken));
    }

    /**
     * 刷新 refresh token，成功则返回新的 access token 和 refresh token；
     * 前端接收到401后会尝试调用这里
     */
    @PostMapping("/refresh")
    public Result<TokenRefreshVO> refresh(@RequestParam String refreshToken) {
        return Result.success(tokenService.refresh(refreshToken));
    }

    /**
     * 验证码获取接口（手机号 / 邮箱）
     */
    @PostMapping("/otp")
    public Result<Void> getOtp(@RequestBody OtpDTO otpDTO) {
        authService.getOtp(otpDTO);
        return Result.success();
    }

    /**
     * 登录接口
     * 若使用【手机号 + 验证码】登录，则会自动注册，但只会保存手机号信息，密码字段未设置（系统中，密码字段为可选）
     */
    @PostMapping("/sign-in")
    public Result<SignInVO> login(@RequestBody SignInDTO signInDTO) {
        return Result.success(authService.signIn(signInDTO));
    }

    /**
     * 注册接口
     *
     * @return 注册完自动完成登录
     */
    @PostMapping("/register")
    public Result<SignInVO> register(@RequestBody RegisterDTO registerDTO) {
        return Result.success(authService.register(registerDTO));
    }

    /**
     * 更新用户认证信息接口：仅支持修改：用户名 和 邮箱
     * 仅远程调用：需要确保在e-dove-user中已经验证邮箱的验证码，验证用户名是否能在
     */
    @PutMapping("/update")
    public Result<Void> update(@RequestBody UpdateUserAuthDTO updateUserAuthDTO) {
        authService.updateUserAuth(updateUserAuthDTO);
        return Result.success();
    }

    // TODO 修改密码接口

}
