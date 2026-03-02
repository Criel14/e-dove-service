package com.criel.edove.user.mq;

import com.criel.edove.common.mq.event.ParcelDelayEvent;
import com.criel.edove.common.mq.event.StockNotifyEvent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

/**
 * 消息队列消费者
 */
@RequiredArgsConstructor
@Configuration
public class EventHandlerConfig {

    private final Logger LOGGER = LoggerFactory.getLogger(EventHandlerConfig.class);

    /**
     * 通知用户包裹入库
     */
    @Bean
    public Consumer<StockNotifyEvent> stockNotify() {
        return (event) -> {
            LOGGER.info(
                    "短信通知用户(phone = {})：包裹(parcelId = {})进入门店(storeId = {})", event.getPhone(),
                    event.getParcelId(), event.getStoreId()
            );

            String message = "【鸽巢驿站】您的包裹已到达" + event.getStoreName() + "，取件码为" + event.getPickCode();
            sendMessage(message);
        };
    }

    /**
     * 通知用户包裹滞留
     */
    @Bean
    public Consumer<ParcelDelayEvent> parcelDelay() {
        return (event) -> {
            LOGGER.info(
                    "短信通知用户(phone = {})：包裹(parcelId = {})在门店(storeId = {})滞留", event.getPhone(),
                    event.getParcelId(), event.getStoreId()
            );

            String message = "【鸽巢驿站】您的包裹在驿站停留太久了，已被标记为滞留件，请到" + event.getStoreName() + "取件或联系工作人员退回包裹";
            sendMessage(message);
        };
    }

    /**
     * 发送通知短信
     */
    private void sendMessage(String message) {
        // TODO 使用sms服务发送短信
        LOGGER.info("短信内容：\"{}\"", message);
    }

}
