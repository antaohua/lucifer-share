package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.TradeRuleGroupEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TradeRuleGroupDao extends JpaBaseDao<Long, TradeRuleGroupEntity> {
    @Override
    public Class<TradeRuleGroupEntity> getEntityClass() {
        return TradeRuleGroupEntity.class;
    }
}
