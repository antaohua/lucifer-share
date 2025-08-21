package com.muniu.cloud.lucifer.share.service.mapper;

import com.baomidou.mybatisplus.core.mapper.Mapper;
import com.muniu.cloud.lucifer.share.service.entity.TdShareHist;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 股票历史行情数据 Mapper 接口
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
public interface TdShareHistMapper extends BaseShardingMapper<TdShareHist> {




    int insertOrUpdateBatch(@Param("list") List<TdShareHist> list, @Param("year") int year);

    int insertOrUpdate(@Param("item") TdShareHist tdShareHist, @Param("year") int year);


    /**
     * 查询数据截止到上个交易日的股票代码
     */
    List<String> selectLastDateShareCodeExcludingToday(@Param("date") int date, @Param("yesterday") int yesterday);

    /**
     * 查询某只股票最后一条数据
     * */
    TdShareHist selectShareLastDate(@Param("shareCode") String shareCode, @Param("year") int year);
}
