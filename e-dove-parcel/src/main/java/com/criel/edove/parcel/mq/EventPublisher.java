package com.criel.edove.parcel.mq;

import com.criel.edove.common.mq.event.ParcelDelayEvent;
import com.criel.edove.common.mq.event.ShelfUpdateEvent;
import com.criel.edove.common.mq.event.StockNotifyEvent;
import com.criel.edove.parcel.constant.BindingNameConstant;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * 消息队列生产者
 */
@Component
@RequiredArgsConstructor
public class EventPublisher {

    private final StreamBridge streamBridge;

    /**
     * 通知扣减货架层的【当前包裹数】
     */
    public void sendShelfUpdate(ShelfUpdateEvent event) {
        streamBridge.send(
                BindingNameConstant.SHELF_UPDATE_OUT, // 绑定名，包括消息的 Topic
                event // 发送的消息，根据配置会自动序列化为JSON
        );
    }

    /**
     * 通知用户包裹入库
     */
    public void sendStockNotify(StockNotifyEvent event) {
        streamBridge.send(
                BindingNameConstant.STOCK_NOTIFY_OUT,
                event
        );
    }

    /**
     * 通知用户包裹滞留
     */
    public void sendParcelDelay(ParcelDelayEvent event) {
        streamBridge.send(
                BindingNameConstant.PARCEL_DELAY_OUT,
                event
        );
    }

}
