package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

@Data
@ToString
public class TdShareHist {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 成交额 单位: 元
     */
    private BigDecimal amount;

    /**
     * 振幅 单位: %
     */
    private BigDecimal amplitude;

    /**
     * 涨跌额 单位: 元
     */
    private BigDecimal changeAmount;

    /**
     * 涨跌幅 单位: %
     */
    private BigDecimal changeRate;

    /**
     * 收盘价 单位: 元
     */
    private BigDecimal closePrice;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * 日期
     */
    private Integer date;

    /**
     * 最高价 单位: 元
     */
    private BigDecimal highPrice;

    /**
     * 最低价 单位: 元
     */
    private BigDecimal lowPrice;

    /**
     * 开盘价 单位: 元
     */
    private BigDecimal openPrice;

    /**
     * 股票代码
     */
    private String shareCode;

    /**
     * 换手率 单位: %
     */
    private BigDecimal turnoverRate;

    /**
     * 成交量 单位: 手
     */
    private BigDecimal volume;

    /**
     * 跌停价
     */
    private BigDecimal limitDown;

    /**
     * 涨停价
     */
    private BigDecimal limitUp;

    /**
     * 昨收
     */
    private BigDecimal previousClose;


    public TdShareHist() {
    }

    public TdShareHist(TdShareMarket shareMarketEntity, long createTime) {
        setShareCode(shareMarketEntity.getShareCode());
        setDate(shareMarketEntity.getDate());
        setId(shareMarketEntity.getShareCode() + "_" + shareMarketEntity.getDate());
        setOpenPrice(shareMarketEntity.getOpeningPrice());
        setClosePrice(shareMarketEntity.getLatestPrice());
        setHighPrice(shareMarketEntity.getHighest());
        setLowPrice(shareMarketEntity.getLowest());
        setVolume(shareMarketEntity.getVolume());
        setAmount(shareMarketEntity.getTurnover());
        setAmplitude(shareMarketEntity.getAmplitude());
        setChangeRate(shareMarketEntity.getChangeRate());
        setChangeAmount(shareMarketEntity.getChangeAmount());
        setTurnoverRate(shareMarketEntity.getTurnoverRate());
        setLimitUp(shareMarketEntity.getLimitUp());
        setLimitDown(shareMarketEntity.getLimitDown());
        setPreviousClose(shareMarketEntity.getPreviousClose());
        setCreateTime(createTime);
    }



}
