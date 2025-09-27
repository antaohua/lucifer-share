package com.muniu.cloud.lucifer.share.service.impl;

import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingService;
import com.muniu.cloud.lucifer.share.service.config.ScheduledInterface;
import com.muniu.cloud.lucifer.share.service.entity.TdStockRealTime;
import com.muniu.cloud.lucifer.share.service.mapper.TdStockRealTimeMapper;
import org.springframework.stereotype.Service;

@Service
public class TdStockRealTimeService extends BaseShardingService<TdStockRealTimeMapper, TdStockRealTime> implements ScheduledInterface {


    @Override
    public void scheduled() {

    }
}
