package com.muniu.cloud.lucifer.share.service.entity;

import java.math.BigDecimal;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("market_fund_flow")
public class MarketFundFlow {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private Integer tradeDate;  // 交易日期，格式：20240925
    
    @TableField("market_type")
    private String marketType;  // 市场类型：SH-上证，SZ-深证
    
    private BigDecimal closingPrice;  // 收盘价
    private BigDecimal changeRate;    // 涨跌幅
    
    private BigDecimal mainNetInflow;        // 主力净流入-净额
    private BigDecimal mainNetInflowRate;    // 主力净流入-净占比
    
    private BigDecimal superNetInflow;       // 超大单净流入-净额
    private BigDecimal superNetInflowRate;   // 超大单净流入-净占比
    
    private BigDecimal largeNetInflow;       // 大单净流入-净额
    private BigDecimal largeNetInflowRate;   // 大单净流入-净占比
    
    private BigDecimal mediumNetInflow;      // 中单净流入-净额
    private BigDecimal mediumNetInflowRate;  // 中单净流入-净占比
    
    private BigDecimal smallNetInflow;       // 小单净流入-净额
    private BigDecimal smallNetInflowRate;   // 小单净流入-净占比

}
