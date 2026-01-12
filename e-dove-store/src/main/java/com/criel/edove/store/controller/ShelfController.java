package com.criel.edove.store.controller;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.store.dto.LayerReduceCountDTO;
import com.criel.edove.store.dto.ParcelCheckInDTO;
import com.criel.edove.store.dto.ShelfDTO;
import com.criel.edove.store.dto.ShelfQueryDTO;
import com.criel.edove.store.service.ShelfService;
import com.criel.edove.store.vo.ParcelCheckInVO;
import com.criel.edove.store.vo.ShelfAndLayerVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 货架 Controller
 *
 */
// TODO（定时任务）远程调用查找当前门店一周前的包裹，通知店员从货架取出并放到包裹滞留处
// TODO（定时任务）当前门店的所有货架的每一层的【当天最大序号】置为0
@RestController
@RequiredArgsConstructor
@RequestMapping("/shelf")
public class ShelfController {

    private final ShelfService shelfService;

    /**
     * （店长 / 店员）新建货架 + 货架层
     * 并根据层数自动创建货架层，最大编号上限默认为999
     */
    @PostMapping("/create")
    public Result<Void> createShelf(@RequestBody ShelfDTO shelfDTO) {
        shelfService.createShelf(shelfDTO);
        return Result.success();
    }

    /**
     * （店长 / 店员）分页查询货架 + 货架层
     */
    // TODO 这个接口的sql比较乱，待测试
    @GetMapping("/query")
    public Result<PageResult<ShelfAndLayerVO>> queryShelfAndLayer(ShelfQueryDTO shelfQueryDTO) {
        return Result.success(shelfService.queryShelfAndLayer(shelfQueryDTO));
    }

    /**
     * （店长 / 店员）修改货架
     * tip: 货架不能删除，只能启用和停用，停用后，不会再有新的包裹分配到该货架
     */
    @PutMapping("/update")
    public Result<Void> updateShelf(@RequestBody ShelfDTO shelfDTO) {
        shelfService.updateShelf(shelfDTO);
        return Result.success();
    }

    // TODO （店长 / 店员）修改货架层

    /**
     * （仅远程调用）扣减包裹所在货架层的【当前包裹数】
     */
    @PostMapping("/layer/reduce")
    public Result<Void> layerReduceCount(@RequestBody LayerReduceCountDTO layerReduceCountDTO) {
        shelfService.layerReduceCount(layerReduceCountDTO);
        return Result.success();
    }

    /**
     * （仅远程调用）为包裹选择合适的货架层，并生成取件码
     *
     * @return 取件码
     */
    @PostMapping("/choose")
    public Result<ParcelCheckInVO> parcelCheckIn(@RequestBody ParcelCheckInDTO parcelCheckInDTO) {
        return Result.success(shelfService.parcelCheckIn(parcelCheckInDTO));
    }

}
