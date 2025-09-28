package com.muniu.cloud.lucifer.share.service.mapper;

import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingMapper;
import com.muniu.cloud.lucifer.share.service.entity.TdIndexHist;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 指数历史行情数据 Mapper 接口
 * </p>
 */
public interface TdIndexHistMapper extends BaseShardingMapper<TdIndexHist> {

    /**
     * 批量插入或更新数据
     * @param list 数据列表
     * @param year 年份
     * @return 影响行数
     */
    int insertOrUpdateBatch(@Param("list") List<TdIndexHist> list, @Param("year") int year);

    /**
     * 查询指数最后一条数据
     * @param indexCode 指数代码
     * @param year 年份
     * @return 指数历史数据
     */
    TdIndexHist selectIndexLastDate(@Param("indexCode") String indexCode, @Param("year") int year);
} 