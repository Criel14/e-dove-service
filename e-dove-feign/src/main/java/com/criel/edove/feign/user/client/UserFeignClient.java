package com.criel.edove.feign.user.client;

import com.criel.edove.common.result.Result;
import com.criel.edove.feign.user.dto.UserInfoDTO;
import com.criel.edove.feign.user.vo.UserInfoVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * e-dove-user模块的远程调用
 */
@FeignClient("e-dove-user")
public interface UserFeignClient {

    /**
     * 创建新用户信息
     */
    @PostMapping("/user/create")
    Result<UserInfoVO> createUserInfo(@RequestBody UserInfoDTO userInfoDTO);

}
