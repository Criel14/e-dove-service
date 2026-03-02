package com.criel.edove.common.mq.event;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.io.Serializable;

/**
 * 包裹滞留通知消息
 */
@EqualsAndHashCode(callSuper = true)
@Data
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class ParcelDelayEvent extends BaseEvent {

    /**
     * 收件人手机号
     */
    String phone;

    /**
     * 包裹ID
     */
    Long parcelId;

    /**
     * 包裹送达门店ID
     */
    Long storeId;

    /**
     * 包裹送达门店名称
     */
    String storeName;

}
