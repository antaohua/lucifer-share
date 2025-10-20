package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.ShareRuleGroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ShareRuleGroupDao extends JpaBaseDao<Long, ShareRuleGroupEntity> {
    @Override
    public Class<ShareRuleGroupEntity> getEntityClass() {
        return ShareRuleGroupEntity.class;
    }
}
