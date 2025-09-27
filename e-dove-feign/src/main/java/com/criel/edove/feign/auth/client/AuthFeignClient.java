package com.criel.edove.feign.auth.client;

import com.criel.edove.common.context.UserInfoContext;
import com.criel.edove.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * e-dove-auth模块的远程调用
 */
@FeignClient("e-dove-auth")
public interface AuthFeignClient {

    /**
     * 校验 access token，成功则返回用户信息；
     */
    @GetMapping("/validate")
    Result<UserInfoContext> validateAccessToken(@RequestParam String accessToken);

}
