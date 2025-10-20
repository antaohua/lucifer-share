package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.MarketFundFlowEntity;
import org.springframework.stereotype.Repository;

@Repository
public class MarketFundFlowDao extends JpaBaseDao<Long,MarketFundFlowEntity> {
    @Override
    public Class<MarketFundFlowEntity> getEntityClass() {
        return MarketFundFlowEntity.class;
    }
}
