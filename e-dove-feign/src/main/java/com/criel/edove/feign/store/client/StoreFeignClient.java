package com.criel.edove.feign.store.client;

import com.criel.edove.common.result.Result;
import com.criel.edove.feign.store.dto.LayerReduceCountDTO;
import com.criel.edove.feign.store.dto.ParcelCheckInDTO;
import com.criel.edove.feign.store.vo.ParcelCheckInVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * e-dove-store模块的远程调用
 */
@FeignClient("e-dove-store")
public interface StoreFeignClient {

    /**
     * （仅远程调用）扣减包裹所在货架层的【当前包裹数】
     */
    @PostMapping("/shelf/layer/reduce")
    Result<Void> layerReduceCount(@RequestBody LayerReduceCountDTO layerReduceCountDTO);

    /**
     * （仅远程调用）为包裹选择合适的货架层，并生成取件码
     * @return 取件码
     */
    @PostMapping("/shelf/choose")
    Result<ParcelCheckInVO> parcelCheckIn(@RequestBody ParcelCheckInDTO parcelCheckInDTO);

}
