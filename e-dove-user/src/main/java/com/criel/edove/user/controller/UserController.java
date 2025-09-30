package com.criel.edove.user.controller;

import com.criel.edove.common.result.Result;
import com.criel.edove.user.dto.UpdateUserInfoDTO;
import com.criel.edove.user.dto.UserInfoDTO;
import com.criel.edove.user.service.UserInfoService;
import com.criel.edove.user.vo.UserInfoVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

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

    private final UserInfoService userInfoService;

    /**
     * 测试连接
     */
    @GetMapping("/ping")
    public Result<Object> ping() {
        return Result.success();
    }

    /**
     * 创建新用户信息
     * 仅远程调用：需要先在e-dove-auth创建用户认证信息，所以参数中的userId不可为null
     */
    @PostMapping("/create")
    public Result<UserInfoVO> createUserInfo(@RequestBody UserInfoDTO userInfoDTO) {
        return Result.success(userInfoService.createUserInfo(userInfoDTO));
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<UserInfoVO> getUserInfo() {
        return Result.success(userInfoService.getUserInfo());
    }

    /**
     * 修改用户信息
     * @param updateUserInfoDTO 只允许修改：用户名、邮箱、头像
     */
    @PostMapping("/update")
    public Result<UserInfoVO> updateUserInfo(@RequestBody UpdateUserInfoDTO updateUserInfoDTO) {
        return  Result.success(userInfoService.updateUserInfo(updateUserInfoDTO));
    }


    // TODO 头像上传接口（可能要写在其他微服务里）
}
