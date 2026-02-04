package com.criel.edove.parcel.controller;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.parcel.dto.*;
import com.criel.edove.parcel.service.ParcelService;
import com.criel.edove.parcel.vo.CheckOutVO;
import com.criel.edove.parcel.vo.ParcelVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 包裹 Controller
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/parcel")
public class ParcelController {

    private final ParcelService parcelService;

    /**
     * 出库：机器调用或管理员手动出库
     * @return 返回剩余的包裹数量
     */
    @PostMapping("/out")
    public Result<CheckOutVO> checkOut(@RequestBody CheckOutDTO checkOutDTO) {
        return Result.success(parcelService.checkOut(checkOutDTO));
    }

    /**
     * 入库
     */
    @PostMapping("/in")
    public Result<Void> checkIn(@RequestBody CheckInDTO checkInDTO) {
        parcelService.checkIn(checkInDTO);
        return Result.success();
    }

    /**
     * 管理端分页查询门店包裹信息
     */
    @GetMapping("/admin/info")
    public Result<PageResult<ParcelVO>> adminInfo(ParcelAdminQueryDTO parcelAdminQueryDTO) {
        return Result.success(parcelService.adminInfo(parcelAdminQueryDTO));
    }

    /**
     * 用户端分页查询个人包裹信息
     */
    @GetMapping("/user/info")
    public Result<PageResult<ParcelVO>> userInfo(ParcelUserQueryDTO parcelUserQueryDTO) {
        return Result.success(parcelService.userInfo(parcelUserQueryDTO));
    }

    /**
     * 运单号查包裹
     */
    @GetMapping("/{trackingNumber}")
    public Result<ParcelVO> queryByTrackingNumber(@PathVariable String trackingNumber) {
        return Result.success(parcelService.queryByTrackingNumber(trackingNumber));
    }


    /**
     * 在数据库里生成指定数量的随机包裹（暂定为：生成的包裹全部送到【用户所在的门店】）
     */
    @PostMapping("/generate")
    public Result<Void> generate(GenerateDTO generateDTO) {
        parcelService.generate(generateDTO.getCount());
        return Result.success();
    }
}
