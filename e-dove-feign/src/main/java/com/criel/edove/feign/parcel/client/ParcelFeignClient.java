package com.criel.edove.feign.parcel.client;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.feign.parcel.dto.ParcelAdminQueryDTO;
import com.criel.edove.feign.parcel.dto.ParcelUserQueryDTO;
import com.criel.edove.feign.parcel.vo.ParcelVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * e-dove-parcel模块的远程调用
 */
@FeignClient("e-dove-parcel")
public interface ParcelFeignClient {

    /**
     * 运单号查包裹
     */
    @GetMapping("/parcel/{trackingNumber}")
    Result<ParcelVO> queryByTrackingNumber(@PathVariable String trackingNumber);

    /**
     * 管理端分页查询门店包裹信息
     */
    @GetMapping("/parcel/admin/info")
    Result<PageResult<ParcelVO>> adminInfo(@SpringQueryMap ParcelAdminQueryDTO parcelAdminQueryDTO);

    /**
     * 用户端分页查询个人包裹信息
     */
    @GetMapping("/parcel/user/info")
    Result<PageResult<ParcelVO>> userInfo(ParcelUserQueryDTO parcelUserQueryDTO);

}
