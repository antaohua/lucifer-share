package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 指数历史行情数据
 */
@Data
@ToString
public class TdIndexHist {

    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    /**
     * 指数代码
     */
    private String indexCode;

    /**
     * 日期
     */
    private Integer date;

    /**
     * 开盘价 单位: 元
     */
    private BigDecimal openPrice;

    /**
     * 收盘价 单位: 元
     */
    private BigDecimal closePrice;

    /**
     * 最高价 单位: 元
     */
    private BigDecimal highPrice;

    /**
     * 最低价 单位: 元
     */
    private BigDecimal lowPrice;

    /**
     * 成交量
     */
    private BigDecimal volume;

    /**
     * 成交额 单位: 元
     */
    private BigDecimal amount;

    /**
     * 创建时间
     */
    private Long createTime;

} 