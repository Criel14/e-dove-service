package com.criel.edove.user.controller;

import com.criel.edove.common.dto.PingDTO;
import com.criel.edove.common.result.Result;
import com.criel.edove.common.vo.PingVO;
import com.criel.edove.user.dto.UpdateUserInfoDTO;
import com.criel.edove.user.dto.UserInfoDTO;
import com.criel.edove.user.service.BarcodeService;
import com.criel.edove.user.service.UserInfoService;
import com.criel.edove.user.vo.IdentityBarcodeVO;
import com.criel.edove.user.vo.UserInfoVO;
import com.criel.edove.user.vo.VerifyBarcodeVO;
import com.google.zxing.WriterException;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

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
    private final BarcodeService barcodeService;

    /**
     * 连接测试
     */
    @GetMapping("/ping")
    public Result<PingVO> ping(@RequestParam PingDTO pingDTO) {
        return Result.success(new PingVO(pingDTO.getMessage()));
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
     * 查询用户所属门店id
     */
    @GetMapping("/store-id")
    public Result<Long> getUserStoreId() {
        return Result.success(userInfoService.getUserStoreId());
    }

    /**
     * 修改用户信息
     *
     * @param updateUserInfoDTO 只允许修改：用户名、邮箱、头像
     */
    @PutMapping("/update")
    public Result<UserInfoVO> updateUserInfo(@RequestBody UpdateUserInfoDTO updateUserInfoDTO) {
        return Result.success(userInfoService.updateUserInfo(updateUserInfoDTO));
    }

    /**
     * 修改用户所属门店专用接口：绑定 / 解绑
     * tip：远程调用前需要检查门店是否存在，且解绑时已判断用户是否绑定该门店
     */
    @PutMapping("/update-store")
    public Result<Void> updateStoreBind(@RequestParam Long storeId) {
        userInfoService.updateStoreBind(storeId);
        return Result.success();
    }

    /**
     * 生成身份码条形码接口
     *
     * @return base64编码的条形码图片
     */
    @GetMapping("/barcode-create")
    public Result<IdentityBarcodeVO> generateBarcode() throws IOException, WriterException {
        return Result.success(barcodeService.generateUserBarcodeBase64());
    }

    /**
     * 验证身份码条形码接口
     * 仅远程调用：出库时使用
     */
    @GetMapping("/barcode-verify")
    public Result<VerifyBarcodeVO> verifyBarcode(@RequestParam String code) {
        return Result.success(barcodeService.verifyIdentityBarcode(code));
    }


    // TODO 头像上传接口（可能要写在其他微服务里）
}
