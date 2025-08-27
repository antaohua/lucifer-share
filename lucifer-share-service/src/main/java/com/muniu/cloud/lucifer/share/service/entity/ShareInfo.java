package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
@Entity
@Table(name = "share_info")
@Data
@ToString
@TableName("share_info")
public class ShareInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(name = "id", length = 10, nullable = false)
    private String id;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private Long createTime;

    /**
     * 交易所
     */
    @Column(name = "exchange", length = 10, nullable = false)
    private String exchange;

    /**
     * 流通市值
     */
    @Column(name = "float_cap")
    private Long floatCap;

    /**
     * 流通股本
     */
    @Column(name = "float_shares")
    private Long floatShares;

    /**
     * 历史更新时间
     */
    @Column(name = "history_update_date")
    private Integer historyUpdateDate;

    /**
     * 股上市日期
     */
    @Column(name = "list_date")
    private Integer listDate;

    /**
     * 市值
     */
    @Column(name = "market_cap")
    private Long marketCap;

    /**
     * 其他更新时间
     */
    @Column(name = "other_update_time")
    private Long otherUpdateTime;

    /**
     * 板块
     */
    @Column(name = "section", length = 100)
    private String section;

    /**
     * 股简称
     */
    @Column(name = "share_name", length = 100, nullable = false)
    private String shareName;

    /**
     * 股票状态
     */
    @Column(name = "share_status", length = 10, nullable = false)
    private String shareStatus;

    /**
     * 状态更新时间
     */
    @Column(name = "status_update_time")
    private Long statusUpdateTime;

    /**
     * 分时数据更新时间
     */
    @Column(name = "time_line_update_date")
    private Integer timeLineUpdateDate;

    /**
     * 总股本
     */
    @Column(name = "total_shares")
    private Long totalShares;

    @Column(name = "update_time")
    private Long updateTime;

    /**
     * 信息更新时间
     */
    @Column(name = "info_update_time")
    private Long infoUpdateTime;


}
