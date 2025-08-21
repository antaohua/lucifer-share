package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

@Data
@ToString
public class TdShareMarket implements Serializable {


    @Serial
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 日期
     */
    private Integer date;

    /**
     * 请求时间
     */
    private Integer time;

    /**
     * 股票代码
     */
    private String shareCode;

    /**
     * 振幅
     */
    private BigDecimal amplitude;

    /**
     * 涨跌额
     */
    private BigDecimal changeAmount;

    /**
     * 涨跌幅
     */
    private BigDecimal changeRate;

    /**
     * 流通市值
     */
    private BigDecimal circulatingMarketCap;

    private Long createTime;

    /**
     * 市盈率-动态
     */
    private BigDecimal dynamicPe;

    /**
     * 5分钟涨跌
     */
    private BigDecimal fiveMinuteChange;

    /**
     * 最高价
     */
    private BigDecimal highest;

    /**
     * 最新价
     */
    private BigDecimal latestPrice;

    /**
     * 最低价
     */
    private BigDecimal lowest;

    /**
     * 今开
     */
    private BigDecimal openingPrice;

    /**
     * 市净率
     */
    private BigDecimal pbRatio;

    /**
     * 昨收
     */
    private BigDecimal previousClose;

    /**
     * 涨速
     */
    private BigDecimal speed;

    /**
     * 总市值
     */
    private BigDecimal totalMarketCap;

    /**
     * 成交额
     */
    private BigDecimal turnover;

    /**
     * 换手率
     */
    private BigDecimal turnoverRate;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * 量比
     */
    private BigDecimal volumeRatio;

    /**
     * 跌停价
     */
    private BigDecimal limitDown;

    /**
     * 涨停价
     */
    private BigDecimal limitUp;
}
