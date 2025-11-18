package com.criel.edove.store.controller;

import com.criel.edove.common.result.Result;
import com.criel.edove.store.dto.ShelfDTO;
import com.criel.edove.store.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public Result<Object> createShelf(ShelfDTO shelfDTO) {
        shelfService.createShelf(shelfDTO);
        return Result.success();
    }


    // TODO （店长 / 店员）查询货架 + 货架层

    // TODO （店长 / 店员）修改货架 / 货架层

    // TODO （店长 / 店员）删除货架 / 货架层（只能停用，不能删除数据？）

}
