package com.muniu.cloud.lucifer.share.service.mapper;

import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingMapper;
import com.muniu.cloud.lucifer.share.service.entity.ConceptMarketEntity;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 概念板块Mapper接口
 */
@Mapper
public interface TdConceptMarketMapper extends BaseShardingMapper<ConceptMarketEntity> {

    /**
     * 创建分表
     * @param month 月份，格式为yyyyMM
     */
    void createTable(@Param("month") String month);


    /**
     * 批量插入或更新概念板块数据
     * @param boardList 概念板块数据列表
     * @param month 月份，格式为yyyyMM
     * @return 影响行数
     */
    int batchInsertOrUpdate(@Param("list") List<ConceptMarketEntity> boardList, @Param("month") String month);
    
    /**
     * 根据概念板块代码和日期查询实时数据
     * @param boardCode 概念板块代码
     * @param startTime 起始时间戳（当日开始）
     * @param endTime 结束时间戳（当日结束）
     * @param month 月份，格式为yyyyMM
     * @return 概念板块实时数据列表
     */
    List<ConceptMarketEntity> getRealtimeDataByCodeAndDate(@Param("boardCode") String boardCode,
                                                           @Param("startTime") Long startTime,
                                                           @Param("endTime") Long endTime,
                                                           @Param("month") String month);
}
