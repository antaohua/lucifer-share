package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.commons.model.constants.Condition;
import com.muniu.cloud.lucifer.share.service.entity.TradeIndexEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TradeIndexDao extends JpaBaseDao<String, TradeIndexEntity> {
    @Override
    public Class<TradeIndexEntity> getEntityClass() {
        return TradeIndexEntity.class;
    }


    public void updateHistUpdate(String indexCode, Integer indexHistUpdate) {
        updateByProperty(
                Map.of("indexHistUpdate", indexHistUpdate),
                List.of(Condition.build("indexCode", indexCode))
        );
    }
}
