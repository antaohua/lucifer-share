package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
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
@Data
@ToString
@TableName("share_info")
public class ShareInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String id;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 交易所
     */
    private String exchange;

    /**
     * 流通市值
     */
    private Long floatCap;

    /**
     * 流通股本
     */
    private Long floatShares;

    /**
     * 历史更新时间
     */
    private Integer historyUpdateDate;

    /**
     * 股上市日期
     */
    private Integer listDate;

    /**
     * 市值
     */
    private Long marketCap;

    /**
     * 其他更新时间
     */
    private Long otherUpdateTime;

    /**
     * 板块
     */
    private String section;

    /**
     * 股简称
     */
    private String shareName;

    /**
     * 股票状态
     */
    private String shareStatus;

    /**
     * 状态更新时间
     */
    private Long statusUpdateTime;

    /**
     * 分时数据更新时间
     */
    private Integer timeLineUpdateDate;

    /**
     * 总股本
     */
    private Long totalShares;

    private Long updateTime;

    /**
     * 信息更新时间
     */
    private Long infoUpdateTime;


}
