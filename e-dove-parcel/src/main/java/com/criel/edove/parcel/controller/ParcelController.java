package com.criel.edove.parcel.controller;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.parcel.dto.CheckInDTO;
import com.criel.edove.parcel.dto.CheckOutDTO;
import com.criel.edove.parcel.dto.GenerateDTO;
import com.criel.edove.parcel.dto.ParcelQueryDTO;
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
    public Result<PageResult<ParcelVO>> adminInfo(ParcelQueryDTO parcelQueryDTO) {
        return Result.success(parcelService.adminInfo(parcelQueryDTO));
    }

    /**
     * 用户端分页查询个人包裹信息
     */
    @GetMapping("/user/info")
    public Result<PageResult<ParcelVO>> userInfo(ParcelQueryDTO parcelQueryDTO) {
        return Result.success(parcelService.userInfo(parcelQueryDTO));
    }

    /**
     * 在数据库里生成指定数量的随机包裹（暂定为生成的包裹全部送到用户所在的门店）
     */
    @PostMapping("generate")
    public Result<Void> generate(GenerateDTO generateDTO) {
        parcelService.generate(generateDTO.getCount());
        return Result.success();
    }
}
