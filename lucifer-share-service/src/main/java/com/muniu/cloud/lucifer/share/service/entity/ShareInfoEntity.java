package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseCustomIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaUpdateCloumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serial;

/**
 * <p>
 *
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "share_info")
@Data
@ToString
public class ShareInfoEntity extends BaseCustomIdEntity implements JpaCreateColumn, JpaUpdateCloumn {

    @Serial
    private static final long serialVersionUID = 1L;

    private long createTime;

    private long updateTime;

    @Column(name = "exchange", length = 10, nullable = false, comment = "交易所")
    private String exchange;

    @Column(name = "float_cap", comment = "流通市值")
    private Long floatCap;

    @Column(name = "float_shares", comment = "流通股本")
    private Long floatShares;

    @Column(name = "history_update_date", comment = "历史更新时间")
    private Integer historyUpdateDate;

    @Column(name = "list_date", comment = "股上市日期")
    private Integer listDate;

    @Column(name = "market_cap", comment = "市值")
    private Long marketCap;

    @Column(name = "other_update_time",comment = "其他更新时间")
    private Long otherUpdateTime;

    @Column(name = "section", length = 100, comment = "板块")
    private String section;

    @Column(name = "share_name", length = 100, nullable = false, comment = "股简称")
    private String shareName;

    @Column(name = "share_status", length = 10, nullable = false, comment = "股票状态")
    private String shareStatus;

    @Column(name = "status_update_time", comment = "状态更新时间")
    private Long statusUpdateTime;

    @Column(name = "time_line_update_date", comment = "分时数据更新时间")
    private Integer timeLineUpdateDate;

    @Column(name = "total_shares", comment = "总股本")
    private Long totalShares;

    @Column(name = "info_update_date", comment = "信息更新时间")
    private Integer infoUpdateDate;


}
