package com.muniu.cloud.lucifer.share.service.dao;

import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.commons.model.constants.Condition;
import com.muniu.cloud.lucifer.commons.model.constants.Operator;
import com.muniu.cloud.lucifer.share.service.entity.TradeBoardConsEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class TradeBoardConsDao extends JpaBaseDao<Long, TradeBoardConsEntity> {
    @Override
    public Class<TradeBoardConsEntity> getEntityClass() {
        return TradeBoardConsEntity.class;
    }


    public int updateInvalidStocks(String boardCode, List<String> toInvalidateStockCodes) {
        if (toInvalidateStockCodes == null || toInvalidateStockCodes.isEmpty()) {
            return 0;
        }
        List<Condition> conditions = Lists.newArrayList(new Condition("boardCode",boardCode),new Condition("stockCode", Operator.IN, toInvalidateStockCodes));
        return updateByProperty(Map.of("isValid", false), conditions);
    }

}
