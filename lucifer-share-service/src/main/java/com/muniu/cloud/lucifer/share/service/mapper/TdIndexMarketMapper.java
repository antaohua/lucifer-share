package com.muniu.cloud.lucifer.share.service.mapper;

import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingMapper;
import com.muniu.cloud.lucifer.share.service.entity.TdIndexMarket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 指数实时行情数据 Mapper 接口
 * </p>
 */
public interface TdIndexMarketMapper extends BaseShardingMapper<TdIndexMarket> {



    /**
     * 批量插入数据
     * @param list 数据列表
     * @param sharding 分表标识，格式为yyyyMMdd
     * @return 影响行数
     */
    int insertBatch(@Param("list") List<TdIndexMarket> list, @Param("sharding") String sharding);


} 