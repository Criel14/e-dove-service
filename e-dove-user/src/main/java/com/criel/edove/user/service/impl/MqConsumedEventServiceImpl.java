package com.criel.edove.user.service.impl;

import com.criel.edove.user.entity.MqConsumedEvent;
import com.criel.edove.user.mapper.MqConsumedEventMapper;
import com.criel.edove.user.service.MqConsumedEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 消息队列的消息去重表 服务实现类
 * </p>
 *
 * @author Criel
 * @since 2026-03-06
 */
@Service
public class MqConsumedEventServiceImpl extends ServiceImpl<MqConsumedEventMapper, MqConsumedEvent> implements MqConsumedEventService {

}
