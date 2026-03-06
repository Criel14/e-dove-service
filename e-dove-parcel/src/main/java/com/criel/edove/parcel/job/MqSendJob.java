package com.criel.edove.parcel.job;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.criel.edove.common.enumeration.OutboxEventStatusEnum;
import com.criel.edove.common.mq.event.ParcelDelayEvent;
import com.criel.edove.common.mq.event.ShelfUpdateEvent;
import com.criel.edove.common.mq.event.StockNotifyEvent;
import com.criel.edove.parcel.constant.BindingNameConstant;
import com.criel.edove.parcel.entity.OutboxEvent;
import com.criel.edove.parcel.mapper.OutboxEventMapper;
import com.criel.edove.parcel.mq.EventPublisher;
import com.criel.edove.parcel.service.OutboxEventService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * xxl-job定时任务
 */
@Component
@RequiredArgsConstructor
public class MqSendJob {

    private final Logger LOGGER = LoggerFactory.getLogger(MqSendJob.class);

    private final OutboxEventMapper outboxEventMapper;
    private final EventPublisher eventPublisher;

    /**
     * 定时任务：发送消息到消息队列
     * 执行时间：每 5s
     */
    @XxlJob("mqSendJob")
    public void sendEventJob() {

        // 查询100条未发送的消息，因为每5s执行一次，所以就算有漏掉的，也会在下一次发送
        List<OutboxEvent> list =
                outboxEventMapper.selectList(
                        new LambdaQueryWrapper<OutboxEvent>()
                                .eq(OutboxEvent::getStatus, OutboxEventStatusEnum.UNSENT.getCode())
                                .orderByAsc(OutboxEvent::getEventId)
                                .last("LIMIT 100")
                );

        // 根据不同的 binding name 发送消息
        list.forEach(event -> {
            try {
                switch (event.getBindingName()) {
                    case BindingNameConstant.SHELF_UPDATE_OUT -> {
                        ShelfUpdateEvent e = JSONUtil.toBean(event.getPayload(), ShelfUpdateEvent.class);
                        eventPublisher.sendShelfUpdate(e);
                    }
                    case BindingNameConstant.STOCK_NOTIFY_OUT -> {
                        StockNotifyEvent e = JSONUtil.toBean(event.getPayload(), StockNotifyEvent.class);
                        eventPublisher.sendStockNotify(e);
                    }
                    case BindingNameConstant.PARCEL_DELAY_OUT -> {
                        ParcelDelayEvent e = JSONUtil.toBean(event.getPayload(), ParcelDelayEvent.class);
                        eventPublisher.sendParcelDelay(e);
                    }
                }
                event.setStatus(OutboxEventStatusEnum.SENT.getCode());
                outboxEventMapper.updateById(event);

            } catch (Exception ex) {
                LOGGER.error("发送消息失败 eventId={}", event.getEventId(), ex);
            }
        });
    }

}