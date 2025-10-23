package com.muniu.cloud.lucifer.share.service.entity;

import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseSnowflakeIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaUpdateCloumn;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 概念板块数据
 */

@Entity
@Table(name = "concept_market", comment = "概念板块数据")
@Data
@EqualsAndHashCode(callSuper = true)
public class BoardMarketEntity extends BaseSnowflakeIdEntity implements JpaUpdateCloumn, JpaCreateColumn {

    private long updateTime;

    private long createTime;

    @Column(name = "board_code", length = 10, nullable = false, comment = "板块代码")
    private String boardCode;

    @Column(name = "board_name", length = 100, nullable = false, comment = "板块名称")
    private String boardName;

    @Column(name = "latest_price", precision = 15, scale = 2, comment = "最新价")
    private BigDecimal latestPrice;

    @Column(name = "change_amount", precision = 10, scale = 2, comment = "涨跌额")
    private BigDecimal changeAmount;

    @Column(name = "change_rate", precision = 5, scale = 2, comment = "涨跌幅(百分比)")
    private BigDecimal changeRate;

    @Column(name = "total_market_value", comment = "总市值(亿元)")
    private Long totalMarketValue;

    @Column(name = "turnover_rate", precision = 5, scale = 2, comment = "换手率(百分比)")
    private BigDecimal turnoverRate;

    @Column(name = "up_count", comment = "上涨家数")
    private Integer upCount;

    @Column(name = "down_count", comment = "下跌家数")
    private Integer downCount;

    @Column(name = "leading_stock", length = 100, comment = "领涨股票")
    private String leadingStock;

    @Column(name = "leading_stock_change_rate", precision = 5, scale = 2, comment = "领涨股票涨跌幅(百分比)")
    private BigDecimal leadingStockChangeRate;

    @Column(name = "rank_value", comment = "排名")
    private Integer rank;


    public BoardMarketEntity() {
    }

    public BoardMarketEntity(JSONObject item, long currentTime) {
        setRank(item.getInteger("排名"));
        setBoardName(item.getString("板块名称"));
        setBoardCode(item.getString("板块代码"));
        setLatestPrice(item.getBigDecimal("最新价"));
        setChangeAmount(item.getBigDecimal("涨跌额"));
        setChangeRate(item.getBigDecimal("涨跌幅"));
        setTotalMarketValue(item.getLong("总市值"));
        setTurnoverRate(item.getBigDecimal("换手率"));
        setUpCount(item.getInteger("上涨家数"));
        setDownCount(item.getInteger("下跌家数"));
        setLeadingStock(item.getString("领涨股票"));
        setLeadingStockChangeRate(item.getBigDecimal("领涨股票-涨跌幅"));
        setUpdateTime(currentTime);
    }
} 