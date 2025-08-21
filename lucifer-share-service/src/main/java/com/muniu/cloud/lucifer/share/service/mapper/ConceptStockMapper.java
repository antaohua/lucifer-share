package com.muniu.cloud.lucifer.share.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muniu.cloud.lucifer.share.service.entity.BoardStock;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 概念板块成份股关系Mapper
 */
@Mapper
public interface ConceptStockMapper extends BaseMapper<BoardStock> {
    
    /**
     * 批量插入概念板块成份股关系
     * 
     * @param boardStocks 概念板块成份股关系列表
     * @return 插入数量
     */
    int batchInsert(@Param("list") List<BoardStock> boardStocks);
    
    /**
     * 根据概念板块代码获取有效的成份股代码
     * 
     * @param boardCode 概念板块代码
     * @return 成份股代码列表
     */
    List<String> getValidStockCodes(@Param("boardCode") String boardCode);
    
    /**
     * 批量更新成份股状态为无效
     * 
     * @param boardCode 概念板块代码
     * @param stockCodes 股票代码列表
     * @param updateTime 更新时间
     * @return 更新数量
     */
    int batchInvalidate(@Param("boardCode") String boardCode, @Param("stockCodes") List<String> stockCodes, @Param("updateTime") Long updateTime);
} 