package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.TradeRuleItemEntity;
import org.springframework.stereotype.Repository;


@Repository
public class TradeRuleItemDao extends JpaBaseDao<Long, TradeRuleItemEntity> {
    @Override
    public Class<TradeRuleItemEntity> getEntityClass() {
        return TradeRuleItemEntity.class;
    }
}
