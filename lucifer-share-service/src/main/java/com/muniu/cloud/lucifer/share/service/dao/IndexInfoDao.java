package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.commons.model.constants.Condition;
import com.muniu.cloud.lucifer.share.service.entity.IndexInfoEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public class IndexInfoDao extends JpaBaseDao<String,IndexInfoEntity> {
    @Override
    public Class<IndexInfoEntity> getEntityClass() {
        return IndexInfoEntity.class;
    }


    public void updateHistUpdate(String indexCode, Integer indexHistUpdate) {
        updateByProperty(
                Map.of("indexHistUpdate", indexHistUpdate),
                List.of(new Condition("indexCode", indexCode))
        );
    }
}
