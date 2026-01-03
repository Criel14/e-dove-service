package com.criel.edove.parcel.controller;

import com.criel.edove.common.result.Result;
import com.criel.edove.parcel.dto.CheckOutDTO;
import com.criel.edove.parcel.service.ParcelService;
import com.criel.edove.parcel.vo.CheckOutVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<Void> checkIn() {

    }
}
