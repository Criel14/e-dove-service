package com.criel.edove.feign.user.client;

import com.criel.edove.common.dto.PingDTO;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.vo.PingVO;
import com.criel.edove.feign.user.dto.UpdateUserInfoDTO;
import com.criel.edove.feign.user.dto.UserInfoDTO;
import com.criel.edove.feign.user.vo.UserInfoVO;
import com.criel.edove.feign.user.vo.VerifyBarcodeVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

/**
 * e-dove-user模块的远程调用
 */
@FeignClient("e-dove-user")
public interface UserFeignClient {

    /**
     * 连接测试
     */
    @GetMapping("/user/ping")
    Result<PingVO> ping(@RequestParam PingDTO pingDTO);

    /**
     * 创建新用户信息
     */
    @PostMapping("/user/create")
    Result<UserInfoVO> createUserInfo(@RequestBody UserInfoDTO userInfoDTO);

    /**
     * 获取用户信息：需要保证请求头有数据
     */
    @GetMapping("/user/info")
    Result<UserInfoVO> getUserInfo();

    /**
     * 查询用户所属门店id：需要保证请求头有数据
     */
    @GetMapping("/user/store-id")
    Result<Long> getUserStoreId();

    /**
     * 修改用户信息
     *
     * @param updateUserInfoDTO 只允许修改：用户名、邮箱、头像，所属门店
     */
    @PostMapping("/user/update")
    Result<UserInfoVO> updateUserInfo(@RequestBody UpdateUserInfoDTO updateUserInfoDTO);

    /**
     * 修改用户所属门店专用接口：绑定 / 解绑
     * tip：远程调用前需要检查门店是否存在，且解绑时已判断用户是否绑定该门店
     */
    @PutMapping("/user/update-store")
    Result<Object> updateStoreBind(@RequestParam Long storeId);

    /**
     * 验证身份码条形码接口
     * 仅远程调用：出库时使用
     */
    @GetMapping("/user/barcode-verify")
    Result<VerifyBarcodeVO> verifyBarcode(@RequestParam String code);

}
