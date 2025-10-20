package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleItemEntity;
import org.springframework.stereotype.Repository;


@Repository
public class ShareRuleItemDao extends JpaBaseDao<Long, ShareRuleItemEntity> {
    @Override
    public Class<ShareRuleItemEntity> getEntityClass() {
        return ShareRuleItemEntity.class;
    }
}
