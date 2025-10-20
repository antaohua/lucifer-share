package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfoEntity;
import org.springframework.stereotype.Repository;

@Repository
public class ShareInfoDao extends JpaBaseDao<String, ShareInfoEntity> {
    @Override
    public Class<ShareInfoEntity> getEntityClass() {
        return ShareInfoEntity.class;
    }
}
