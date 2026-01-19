package com.criel.edove.parcel.job;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.criel.edove.common.enumeration.ParcelStatusEnum;
import com.criel.edove.parcel.entity.Parcel;
import com.criel.edove.parcel.mapper.ParcelMapper;
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
public class ParcelJob {

    private final ParcelMapper parcelMapper;

    private static final int BATCH_SIZE = 1000; // 每一批处理的包裹数量
    private static final int MAX_STALE_DAYS = 7; // 最大滞留时间

    /**
     * 定时任务：标记入库时间超过【7天】的滞留包裹；
     * 执行时间：每天凌晨3点
     * <p>
     * 用【游标分页】按批次处理：
     * 1. 首先查询条件是【已入库但未出库 && 入库时间在7天前】
     * 2. 结果按 id 游标分页，每次取下一批符合条件的记录，类似滑动窗口
     * 3. 更新后继续推进到下一个窗口，如果查询结果为空，则说明都处理完了，break
     */
    @XxlJob("handleStalePackages")
    public void handleStalePackages() {
        XxlJobHelper.log("定时任务：开始标记滞留包裹");

        LocalDateTime cutoff = LocalDateTime.now().minusDays(MAX_STALE_DAYS);
        int total = 0;
        long lastId = 0L;
        while (true) {
            // 分批查询符合条件的包裹
            LambdaQueryWrapper<Parcel> parcelSelectWrapper = new LambdaQueryWrapper<>();
            parcelSelectWrapper.eq(Parcel::getStatus, ParcelStatusEnum.IN_STORAGE.getCode())
                    .lt(Parcel::getInTime, cutoff)
                    .gt(Parcel::getId, lastId)
                    .orderByAsc(Parcel::getId)
                    .last("limit " + BATCH_SIZE);
            List<Parcel> parcels = parcelMapper.selectList(parcelSelectWrapper);
            if (parcels.isEmpty()) {
                break;
            }

            // 获取id列表
            List<Long> ids = parcels.stream()
                    .map(Parcel::getId)
                    .toList();

            // 批量更新状态为【滞留】
            LambdaUpdateWrapper<Parcel> parcelUpdateWrapper = new LambdaUpdateWrapper<>();
            parcelUpdateWrapper.in(Parcel::getId, ids)
                    .eq(Parcel::getStatus, ParcelStatusEnum.IN_STORAGE.getCode())  // 保证状态正确，否则可能有并发问题
                    .set(Parcel::getStatus, ParcelStatusEnum.STALE.getCode());
            parcelMapper.update(null, parcelUpdateWrapper);

            // 日志
            total += ids.size();
            XxlJobHelper.log("本批处理：" + ids.size() + "条，累计：" + total + "条");

            // TODO 通知驿站人员去处理滞留包裹

            // 更新 lastId 作为下一轮游标
            lastId = ids.get(ids.size() - 1);
        }

        XxlJobHelper.log("定时任务：结束标记滞留包裹");
    }

}
