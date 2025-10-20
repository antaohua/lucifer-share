package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseSnowflakeIdEntity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "market_fund_flow")
@Data
public class MarketFundFlowEntity extends BaseSnowflakeIdEntity {
    
    @Column(name = "trade_date", nullable = false)
    private Integer tradeDate;  // 交易日期，格式：20240925
    
    @Column(name = "market_type", length = 10, nullable = false)
    private String marketType;  // 市场类型：SH-上证，SZ-深证
    
    @Column(name = "closing_price", precision = 10, scale = 2)
    private BigDecimal closingPrice;  // 收盘价
    
    @Column(name = "change_rate", precision = 5, scale = 2)
    private BigDecimal changeRate;    // 涨跌幅
    
    @Column(name = "main_net_inflow", precision = 15, scale = 2)
    private BigDecimal mainNetInflow;        // 主力净流入-净额
    
    @Column(name = "main_net_inflow_rate", precision = 5, scale = 2)
    private BigDecimal mainNetInflowRate;    // 主力净流入-净占比
    
    @Column(name = "super_net_inflow", precision = 15, scale = 2)
    private BigDecimal superNetInflow;       // 超大单净流入-净额
    
    @Column(name = "super_net_inflow_rate", precision = 5, scale = 2)
    private BigDecimal superNetInflowRate;   // 超大单净流入-净占比
    
    @Column(name = "large_net_inflow", precision = 15, scale = 2)
    private BigDecimal largeNetInflow;       // 大单净流入-净额
    
    @Column(name = "large_net_inflow_rate", precision = 5, scale = 2)
    private BigDecimal largeNetInflowRate;   // 大单净流入-净占比
    
    @Column(name = "medium_net_inflow", precision = 15, scale = 2)
    private BigDecimal mediumNetInflow;      // 中单净流入-净额
    
    @Column(name = "medium_net_inflow_rate", precision = 5, scale = 2)
    private BigDecimal mediumNetInflowRate;  // 中单净流入-净占比
    
    @Column(name = "small_net_inflow", precision = 15, scale = 2)
    private BigDecimal smallNetInflow;       // 小单净流入-净额
    
    @Column(name = "small_net_inflow_rate", precision = 5, scale = 2)
    private BigDecimal smallNetInflowRate;   // 小单净流入-净占比

}
