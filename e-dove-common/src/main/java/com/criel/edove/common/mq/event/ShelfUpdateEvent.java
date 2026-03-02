package com.criel.edove.common.mq.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 更新货架层【当前包裹数】通知消息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ShelfUpdateEvent extends BaseEvent {

    /**
     * 门店ID
     */
    Long storeId;

    /**
     * 货架编号
     */
    Integer shelfNo;

    /**
     * 货架层编号
     */
    Integer layerNo;

}
