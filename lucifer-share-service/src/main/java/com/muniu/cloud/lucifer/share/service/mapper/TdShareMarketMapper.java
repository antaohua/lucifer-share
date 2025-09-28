package com.muniu.cloud.lucifer.share.service.mapper;

import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingMapper;
import com.muniu.cloud.lucifer.share.service.entity.TdShareMarket;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 实时行情数据 Mapper 接口
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
public interface TdShareMarketMapper extends BaseShardingMapper<TdShareMarket> {

    int insertBatch(@Param("list") List<TdShareMarket> list, @Param("sharding") String sharding);



}
