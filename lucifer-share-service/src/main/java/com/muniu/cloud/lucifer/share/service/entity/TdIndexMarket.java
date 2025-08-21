package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 指数分时数据
 */
@Data
@ToString
public class TdIndexMarket implements Serializable {

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
     * 指数代码
     */
    private String indexCode;

    /**
     * 指数名称
     */
    private String indexName;
    
    /**
     * 最新价
     */
    private BigDecimal latestPrice;

    /**
     * 涨跌额
     */
    private BigDecimal changeAmount;

    /**
     * 涨跌幅
     */
    private BigDecimal changeRate;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * 成交额
     */
    private BigDecimal turnover;

    /**
     * 振幅
     */
    private BigDecimal amplitude;

    /**
     * 最高价
     */
    private BigDecimal highest;

    /**
     * 最低价
     */
    private BigDecimal lowest;

    /**
     * 今开
     */
    private BigDecimal openingPrice;

    /**
     * 昨收
     */
    private BigDecimal previousClose;

    /**
     * 量比
     */
    private BigDecimal volumeRatio;
    
    /**
     * 创建时间
     */
    private Long createTime;


} 