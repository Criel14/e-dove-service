package com.criel.edove.store.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.criel.edove.common.enumeration.ShelfStatusEnum;
import com.criel.edove.store.entity.Shelf;
import com.criel.edove.store.entity.ShelfLayer;
import com.criel.edove.store.mapper.ShelfLayerMapper;
import com.criel.edove.store.mapper.ShelfMapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * xxl-job定时任务
 */
@Component
@RequiredArgsConstructor
public class StoreJob {

    private final ShelfLayerMapper shelfLayerMapper;
    private final ShelfMapper shelfMapper;

    private static final int BATCH_SIZE = 200; // 每一批处理的货架数量（一般有200 * 5 = 1000个货架层要处理）
    private static final int MAX_STALE_DAYS = 7; // 最大滞留时间

    /**
     * 定时任务：所有货架层的【当天最大序号】置为0；
     * 执行时间：每天凌晨3点
     */
    @XxlJob("handleShelfSeq")
    public void handleShelfSeq() {
        XxlJobHelper.log("定时任务：开始重置货架层的【当天最大序号】");

        LocalDateTime cutoff = LocalDateTime.now().minusDays(MAX_STALE_DAYS);
        int total = 0;
        long lastId = 0L;
        while (true) {
            // 分批查询启用中的【货架】
            LambdaQueryWrapper<Shelf> shelfQueryWrapper = new LambdaQueryWrapper<>();
            shelfQueryWrapper.eq(Shelf::getStatus, ShelfStatusEnum.ENABLE.getCode())
                    .gt(Shelf::getId, lastId)
                    .orderByAsc(Shelf::getId)
                    .last("limit " + BATCH_SIZE);
            List<Shelf> shelves = shelfMapper.selectList(shelfQueryWrapper);
            if (shelves.isEmpty()) {
                break;
            }

            // 获取id列表
            List<Long> ids = shelves.stream()
                    .map(Shelf::getId)
                    .toList();

            // 批量更新【货架层】
            LambdaUpdateWrapper<ShelfLayer> layerUpdateWrapper = new LambdaUpdateWrapper<>();
            layerUpdateWrapper.in(ShelfLayer::getShelfId, ids)
                    .set(ShelfLayer::getTodayMaxSeq, 0);
            shelfLayerMapper.update(null, layerUpdateWrapper);

            // 日志
            total += ids.size();
            XxlJobHelper.log("本批处理：" + ids.size() + "条，累计：" + total + "条");

            // 更新 lastId 作为下一轮游标
            lastId = ids.get(ids.size() - 1);
        }

        XxlJobHelper.log("定时任务：结束重置货架层的【当天最大序号】");
    }

}
