package com.criel.edove.store.controller;

import com.criel.edove.common.result.PageResult;
import com.criel.edove.common.result.Result;
import com.criel.edove.store.dto.StoreDTO;
import com.criel.edove.store.dto.StoreBindDTO;
import com.criel.edove.store.service.StoreService;
import com.criel.edove.store.vo.StoreVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 门店 Controller
 *
 * @author Criel
 * @since 2025-10-02
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/store")
public class StoreController {

    private final StoreService storeService;

    /**
     * 分页查询所有门店信息：支持店名搜索
     */
    @GetMapping("/page")
    public Result<PageResult<StoreVO>> page(
            @RequestParam int pageNum,
            @RequestParam int pageSize,
            @RequestParam String storeName) {
        return Result.success(storeService.page(pageNum, pageSize, storeName));
    }

    /**
     * （店长/店员）查询所属门店信息
     * 若未绑定门店，则抛出“未绑定门店异常”，之后用户可以查询门店，并在门店列表中绑定门店
     */
    @GetMapping("/my/info")
    public Result<StoreVO> getStoreInfoByUser() {
        return Result.success(storeService.getStoreInfoByUser());
    }

    /**
     * （店长）新建门店
     */
    @PostMapping("/create")
    public Result<StoreVO> createStore(@RequestBody StoreDTO storeDTO) {
        return Result.success(storeService.createStore(storeDTO));
    }

    /**
     * （店长/店员）绑定门店
     */
    @PostMapping("/bind")
    public Result<Object> bindStore(@RequestBody StoreBindDTO storeBindDTO) {
        storeService.bindStore(storeBindDTO);
        return Result.success();
    }


    // TODO （店长）注销门店

    // TODO （店长）修改门店信息

    // TODO （店长）解绑门店

}
