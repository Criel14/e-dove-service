package com.criel.edove.user.controller;

import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.result.Result;
import com.criel.edove.user.service.AuthService;
import com.criel.edove.user.vo.TokenRefreshVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 权限认证 Controller
 *
 * @author Criel
 * @since 2025-09-22
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/user/auth")
public class AuthController {

    private final AuthService authService;

    /**
     * 校验 access token，成功则返回用户信息；
     * 网关调用
     */
    @GetMapping("/validate")
    public Result<UserInfoContext> validateAccessToken(@RequestParam String accessToken) {
        return Result.success(authService.validateAccessToken(accessToken));
    }

    /**
     * 刷新 refresh token，成功则返回新的 access token 和 refresh token；
     * 前端接收到401后会尝试调用这里
     */
    @PostMapping("/refresh")
    public Result<TokenRefreshVO> refresh(@RequestParam String refreshToken) {
        return authService.refresh(refreshToken);
    }
}
