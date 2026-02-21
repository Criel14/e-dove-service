package com.criel.edove.feign.parcel.client;

import com.criel.edove.common.result.Result;
import com.criel.edove.feign.parcel.vo.ParcelVO;
import org.springframework.cloud.openfeign.FeignClient;
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

}
