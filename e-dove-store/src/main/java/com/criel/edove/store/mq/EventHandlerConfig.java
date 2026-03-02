package com.criel.edove.store.mq;

import com.criel.edove.common.mq.event.ShelfUpdateEvent;
import com.criel.edove.store.dto.LayerReduceCountDTO;
import com.criel.edove.store.service.ShelfService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * 消息队列消费者
 */
@RequiredArgsConstructor
@Configuration
public class EventHandlerConfig {

    private final ShelfService shelfService;

    /**
     * 货架层的【当前包裹数】更新事件处理
     */
    @Bean
    public Consumer<ShelfUpdateEvent> shelfUpdate() {
        return (event) -> {
            shelfService.layerReduceCount(new LayerReduceCountDTO(
                    event.getStoreId(),
                    event.getShelfNo(),
                    event.getLayerNo()
            ));
        };
    }

}
