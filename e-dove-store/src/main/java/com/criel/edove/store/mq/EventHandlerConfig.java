package com.criel.edove.store.mq;

import com.criel.edove.common.mq.event.BaseEvent;
import com.criel.edove.common.mq.event.ShelfUpdateEvent;
import com.criel.edove.store.dto.LayerReduceCountDTO;
import com.criel.edove.store.entity.MqConsumedEvent;
import com.criel.edove.store.service.MqConsumedEventService;
import com.criel.edove.store.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;
import java.util.function.Consumer;

/**
 * 消息队列消费者
 */
@RequiredArgsConstructor
@Configuration
public class EventHandlerConfig {

    private final ShelfService shelfService;
    private final MqConsumedEventService mqConsumedEventService;

    /**
     * 货架层的【当前包裹数】更新事件处理
     */
    @Bean
    public Consumer<ShelfUpdateEvent> shelfUpdate() {
        return (event) -> {
            // 校验消息是否重复消费
            if (!checkMessage(event)) {
                // 直接跳过
                return;
            }

            shelfService.layerReduceCount(new LayerReduceCountDTO(
                    event.getStoreId(),
                    event.getShelfNo(),
                    event.getLayerNo()
            ));
        };
    }

    /**
     * 校验消息是否重复消费
     */
    private boolean checkMessage(BaseEvent event) {
        MqConsumedEvent mqConsumedEvent = new MqConsumedEvent();
        mqConsumedEvent.setEventId(event.getEventId());
        mqConsumedEvent.setProduceTime(event.getOccurredAt());
        mqConsumedEvent.setConsumeTime(LocalDateTime.now());
        return mqConsumedEventService.save(mqConsumedEvent);
    }

}
