package com.muniu.cloud.lucifer.share.service.mapper;

import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingMapper;
import com.muniu.cloud.lucifer.share.service.entity.TdStockRealTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface TdStockRealTimeMapper extends BaseShardingMapper<TdStockRealTime> {


    int insertOrUpdateBatch(@Param("list") List<TdStockRealTime> list, @Param("sharding") int sharding);

    int insertOrUpdate(@Param("item") TdStockRealTime stockRealTimeData, @Param("sharding") int sharding);
}
