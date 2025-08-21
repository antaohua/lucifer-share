package com.muniu.cloud.lucifer.share.service.entity;

import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

/**
 * 概念板块数据
 */
@Data
@Schema(description = "概念板块数据")
public class ConceptMarket {

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "主键ID")
    private Long id;

    /**
     * 板块代码
     */
    @TableField("board_code")
    @Schema(description = "板块代码", example = "BK0623")
    private String boardCode;

    /**
     * 板块名称
     */
    @TableField("board_name")
    @Schema(description = "板块名称", example = "新冠检测")
    private String boardName;

    /**
     * 最新价
     */
    @TableField("latest_price")
    @Schema(description = "最新价", example = "1876.54")
    private BigDecimal latestPrice;

    /**
     * 涨跌额
     */
    @TableField("change_amount")
    @Schema(description = "涨跌额", example = "12.34")
    private BigDecimal changeAmount;

    /**
     * 涨跌幅（百分比）
     */
    @TableField("change_rate")
    @Schema(description = "涨跌幅（百分比）", example = "0.65")
    private BigDecimal changeRate;

    /**
     * 总市值（亿元）
     */
    @TableField("total_market_value")
    @Schema(description = "总市值（亿元）", example = "23687")
    private Long totalMarketValue;

    /**
     * 换手率（百分比）
     */
    @TableField("turnover_rate")
    @Schema(description = "换手率（百分比）", example = "1.23")
    private BigDecimal turnoverRate;

    /**
     * 上涨家数
     */
    @TableField("up_count")
    @Schema(description = "上涨家数", example = "32")
    private Integer upCount;

    /**
     * 下跌家数
     */
    @TableField("down_count")
    @Schema(description = "下跌家数", example = "15")
    private Integer downCount;

    /**
     * 领涨股票
     */
    @TableField("leading_stock")
    @Schema(description = "领涨股票", example = "奥泰生物")
    private String leadingStock;

    /**
     * 领涨股票涨跌幅（百分比）
     */
    @TableField("leading_stock_change_rate")
    @Schema(description = "领涨股票涨跌幅（百分比）", example = "10.00")
    private BigDecimal leadingStockChangeRate;

    /**
     * 排名
     */
    @TableField("rank")
    @Schema(description = "排名", example = "1")
    private Integer rank;

    /**
     * 更新时间（毫秒时间戳）
     */
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