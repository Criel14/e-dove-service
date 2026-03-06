package com.criel.edove.parcel.service.impl;

import com.criel.edove.parcel.entity.OutboxEvent;
import com.criel.edove.parcel.mapper.OutboxEventMapper;
import com.criel.edove.parcel.service.OutboxEventService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Criel
 * @since 2026-03-06
 */
@Service
public class OutboxEventServiceImpl extends ServiceImpl<OutboxEventMapper, OutboxEvent> implements OutboxEventService {

}
