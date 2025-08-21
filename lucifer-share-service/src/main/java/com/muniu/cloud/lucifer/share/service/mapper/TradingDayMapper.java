package com.muniu.cloud.lucifer.share.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muniu.cloud.lucifer.share.service.entity.TradingDay;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 交易日数据访问接口
 */
public interface TradingDayMapper extends BaseMapper<TradingDay> {

    /**
     * 批量保存交易日
     * @param tradingDays 交易日列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<TradingDay> tradingDays);
    
    /**
     * 获取指定日期范围内的所有交易日
     * @param startDay 开始日期
     * @param endDay 结束日期
     * @return 交易日列表
     */
    List<Integer> getTradingDaysBetween(@Param("startDay") Integer startDay, @Param("endDay") Integer endDay);
    
    /**
     * 获取所有交易日
     * @return 所有交易日列表
     */
    List<Integer> getAllTradingDays();

} 