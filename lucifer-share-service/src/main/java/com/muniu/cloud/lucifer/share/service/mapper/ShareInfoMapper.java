package com.muniu.cloud.lucifer.share.service.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
public interface ShareInfoMapper extends BaseMapper<ShareInfo> {


    void updateShareHistoryUpdateDate(@Param("shareCode") String shareCode, @Param("shareHistoryUpdateDate") int shareHistoryUpdateDate);

    void updateShareHistoryUpdateDateBatch(@Param("shareCodes") List<String> shareCodes, @Param("shareHistoryUpdateDate") int shareHistoryUpdateDate);

    int updateShareDemisted(@Param("shareCodes") List<String> shareCodes, @Param("updateTime") long updateTime);

    int insertOrUpdateShareInfo(@Param("shareInfo") ShareInfo shareInfo);
}


