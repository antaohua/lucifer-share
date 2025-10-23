package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseCustomIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "share_index_cons")
@Data
public class ShareIndexConsEntity extends BaseCustomIdEntity {

    @Column(name = "index_code", length = 10, nullable = false, comment = "指数代码")
    private String indexCode;

    @Column(name = "share_code", length = 10, nullable = false, comment = "股票代码")
    private String shareCode;

    @Column(name = "date", nullable = false, comment = "加入日期")
    private Integer date;

    @Column(name = "status", nullable = false, comment = "状态")
    private byte status;
}
