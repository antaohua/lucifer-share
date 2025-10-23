package com.muniu.cloud.lucifer.share.service.entity;


import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseCustomIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaUpdateCloumn;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 指数基本信息
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "index_info")
@Data
@Schema(description = "指数基本信息")
public class IndexInfoEntity extends BaseCustomIdEntity implements JpaCreateColumn, JpaUpdateCloumn {

    private long createTime;

    private long updateTime;

    @Column(name = "display_name", length = 100, nullable = false, comment = "指数名称")
    private String displayName;

    @Column(name = "publish_date", nullable = false, comment = "发布日期，格式：yyyyMMdd")
    private Integer publishDate;

    @Column(name = "source", length = 10, nullable = false, comment = "指数来源")
    private String source;

    @Column(name = "update_date", nullable = false, comment = "更新日期，格式：yyyyMMdd")
    private Integer updateDate;

    @Column(name = "index_hist_update", comment = "指数历史数据最后更新日期")
    private Integer indexHistUpdate;

    @Column(name = "index_const_update", comment = "指数成分股最后更新日期")
    private Integer indexConstUpdate;

    @Column(name = "update_history", nullable = false, comment = "是否更新历史数据：1-更新 0-不更新")
    private Byte updateHistory;

    @Column(name = "update_constituent", nullable = false, comment = "是否更新成分股：1-更新 0-不更新")
    private Byte updateConstituent;

}