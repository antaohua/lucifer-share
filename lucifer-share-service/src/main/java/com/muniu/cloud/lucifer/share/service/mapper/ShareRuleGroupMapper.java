package com.muniu.cloud.lucifer.share.service.mapper;

import com.muniu.cloud.lucifer.share.service.entity.ShareRuleGroup;
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
public interface ShareRuleGroupMapper extends BaseMapper<ShareRuleGroup> {

    List<ShareRuleGroup> queryPage(@Param("userId") String userId, @Param("keyword") String keyword, @Param("offset") int offset, @Param("size") int size,@Param("order") String order, @Param("orderType") boolean orderType);

    List<ShareRuleGroup> getRuleGroupByUser(@Param("userId") String userId, @Param("order") String order, @Param("orderType") boolean orderType);


    int insertRuleGroup(ShareRuleGroup shareRuleGroup);

    int updateRuleGroup(ShareRuleGroup shareRuleGroup);

    int deleteRuleGroup(@Param("id") Long id);
}
