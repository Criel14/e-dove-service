package com.criel.edove.feign.auth.client;

import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.result.Result;
import com.criel.edove.feign.auth.dto.UpdateUserAuthDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * e-dove-auth模块的远程调用
 */
@FeignClient("e-dove-auth")
public interface AuthFeignClient {

    /**
     * 校验 access token，成功则返回用户信息；
     */
    @GetMapping("/auth/validate")
    Result<UserInfoContext> validateAccessToken(@RequestParam String accessToken);

    /**
     * 更新用户认证信息接口：仅支持修改：用户名 和 邮箱
     * 仅远程调用：需要确保在e-dove-user中已经验证邮箱的验证码，验证用户名是否能在
     */
    @PostMapping("/auth/update")
    Result<Object> update(@RequestBody UpdateUserAuthDTO updateUserAuthDTO);

}
