package com.muniu.cloud.lucifer.share.service.dao;

import com.muniu.cloud.lucifer.commons.core.jpa.JpaBaseDao;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;
import org.springframework.stereotype.Repository;

@Repository
public class ShareInfoDao extends JpaBaseDao<String, ShareInfo> {
    @Override
    public Class<ShareInfo> getEntityClass() {
        return ShareInfo.class;
    }
}
