package com.muniu.cloud.lucifer.share.service.mapper;

import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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


    void updateShareHistoryUpdateDate(@Param("shareCode") String shareCode, @Param("updateTime") long updateTime);

    void updateShareHistoryUpdateDateBatch(@Param("shareCodes") List<String> shareCodes, @Param("updateTime") long updateTime);
}


