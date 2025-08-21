package com.muniu.cloud.lucifer.share.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muniu.cloud.lucifer.share.service.entity.IndexInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 指数基本信息 Mapper 接口
 * </p>
 */
public interface IndexInfoMapper extends BaseMapper<IndexInfo> {

    
    /**
     * 批量插入或更新数据
     * @param list 指数信息列表
     * @return 影响行数
     */
    int batchInsertOrUpdate(@Param("list") List<IndexInfo> list);
    
    /**
     * 更新指数的来源、更新历史数据状态和是否更新成分股状态
     * @param indexCode 指数代码
     * @param source 指数来源
     * @param updateHistory 是否更新历史数据
     * @param updateConstituent 是否更新成分股
     * @param updateTime 更新时间
     * @return 更新的记录数
     */
    int updateIndexSettings(@Param("indexCode") String indexCode, @Param("source") String source, @Param("updateHistory") Byte updateHistory, @Param("updateConstituent") Byte updateConstituent, @Param("updateTime") Long updateTime);

    /**
     * 更新指数历史数据时间
     * @param indexCode 指数代码
     * @param indexHistUpdate 历史数据更新时间（毫秒时间戳）
     * @return 影响行数
     */
    int updateHistUpdate(@Param("indexCode") String indexCode, @Param("indexHistUpdate") Integer indexHistUpdate);

    /**
     * 更新指数成分股时间
     * @param indexCode 指数代码
     * @param indexConstUpdate 成分股数据更新时间（毫秒时间戳）
     * @return 影响行数
     */
    int updateConstUpdate(@Param("indexCode") String indexCode, @Param("indexConstUpdate") Integer indexConstUpdate);


}