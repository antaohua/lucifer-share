package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.TradeFundFlowEntity;
import org.springframework.stereotype.Repository;

@Repository
public class TradeFundFlowDao extends JpaBaseDao<Long, TradeFundFlowEntity> {
    @Override
    public Class<TradeFundFlowEntity> getEntityClass() {
        return TradeFundFlowEntity.class;
    }
}
