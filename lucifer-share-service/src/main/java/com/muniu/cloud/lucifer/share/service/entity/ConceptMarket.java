package com.muniu.cloud.lucifer.share.service.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 概念板块数据
 */
@Entity
@Table(name = "concept_market")
@Data
@Schema(description = "概念板块数据")
public class ConceptMarket {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 板块代码
     */
    @Column(name = "board_code", length = 10, nullable = false)
    @TableField("board_code")
    @Schema(description = "板块代码", example = "BK0623")
    private String boardCode;

    /**
     * 板块名称
     */
    @Column(name = "board_name", length = 100, nullable = false)
    @TableField("board_name")
    @Schema(description = "板块名称", example = "新冠检测")
    private String boardName;

    /**
     * 最新价
     */
    @Column(name = "latest_price", precision = 15, scale = 2)
    @TableField("latest_price")
    @Schema(description = "最新价", example = "1876.54")
    private BigDecimal latestPrice;

    /**
     * 涨跌额
     */
    @Column(name = "change_amount", precision = 10, scale = 2)
    @TableField("change_amount")
    @Schema(description = "涨跌额", example = "12.34")
    private BigDecimal changeAmount;

    /**
     * 涨跌幅（百分比）
     */
    @Column(name = "change_rate", precision = 5, scale = 2)
    @TableField("change_rate")
    @Schema(description = "涨跌幅（百分比）", example = "0.65")
    private BigDecimal changeRate;

    /**
     * 总市值（亿元）
     */
    @Column(name = "total_market_value")
    @TableField("total_market_value")
    @Schema(description = "总市值（亿元）", example = "23687")
    private Long totalMarketValue;

    /**
     * 换手率（百分比）
     */
    @Column(name = "turnover_rate", precision = 5, scale = 2)
    @TableField("turnover_rate")
    @Schema(description = "换手率（百分比）", example = "1.23")
    private BigDecimal turnoverRate;

    /**
     * 上涨家数
     */
    @Column(name = "up_count")
    @TableField("up_count")
    @Schema(description = "上涨家数", example = "32")
    private Integer upCount;

    /**
     * 下跌家数
     */
    @Column(name = "down_count")
    @TableField("down_count")
    @Schema(description = "下跌家数", example = "15")
    private Integer downCount;

    /**
     * 领涨股票
     */
    @Column(name = "leading_stock", length = 100)
    @TableField("leading_stock")
    @Schema(description = "领涨股票", example = "奥泰生物")
    private String leadingStock;

    /**
     * 领涨股票涨跌幅（百分比）
     */
    @Column(name = "leading_stock_change_rate", precision = 5, scale = 2)
    @TableField("leading_stock_change_rate")
    @Schema(description = "领涨股票涨跌幅（百分比）", example = "10.00")
    private BigDecimal leadingStockChangeRate;

    /**
     * 排名
     */
    @Column(name = "rank_value") // Changed to rank_value to avoid conflict with SQL keyword RANK
    @TableField("rank_value")
    @Schema(description = "排名", example = "1")
    private Integer rank;

    /**
     * 更新时间（毫秒时间戳）
     */
    @Column(name = "update_time", nullable = false)
    @TableField("update_time")
    @Schema(description = "更新时间（毫秒时间戳）")
    private Long updateTime;

    public ConceptMarket() {
    }

    public ConceptMarket(JSONObject item, long currentTime) {
        setRank(item.getInteger("排名"));
        setBoardName(item.getString("板块名称"));
        setBoardCode(item.getString("板块代码"));
        // 设置价格和变动信息
        setLatestPrice(item.getBigDecimal("最新价"));
        setChangeAmount(item.getBigDecimal("涨跌额"));
        setChangeRate(item.getBigDecimal("涨跌幅"));
        // 设置市场信息
        setTotalMarketValue(item.getLong("总市值"));
        setTurnoverRate(item.getBigDecimal("换手率"));
        // 设置股票统计信息
        setUpCount(item.getInteger("上涨家数"));
        setDownCount(item.getInteger("下跌家数"));
        // 设置领涨股票信息
        setLeadingStock(item.getString("领涨股票"));
        setLeadingStockChangeRate(item.getBigDecimal("领涨股票-涨跌幅"));
        // 设置更新时间
        setUpdateTime(currentTime);
    }
} 