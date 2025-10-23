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

    @Column(name = "trade_date", nullable = false, comment = "交易日期，格式：20240925")
    private Integer tradeDate;

    @Column(name = "market_type", length = 10, nullable = false, comment = "市场类型：SH-上证，SZ-深证")
    private String marketType;

    @Column(name = "closing_price", precision = 10, scale = 2, comment = "收盘价")
    private BigDecimal closingPrice;

    @Column(name = "change_rate", precision = 5, scale = 2, comment = "涨跌幅")
    private BigDecimal changeRate;

    @Column(name = "main_net_inflow", precision = 15, scale = 2, comment = "主力净流入-净额")
    private BigDecimal mainNetInflow;

    @Column(name = "main_net_inflow_rate", precision = 5, scale = 2, comment = "主力净流入-净占比")
    private BigDecimal mainNetInflowRate;

    @Column(name = "super_net_inflow", precision = 15, scale = 2, comment = "超大单净流入-净额")
    private BigDecimal superNetInflow;

    @Column(name = "super_net_inflow_rate", precision = 5, scale = 2, comment = "超大单净流入-净占比")
    private BigDecimal superNetInflowRate;

    @Column(name = "large_net_inflow", precision = 15, scale = 2, comment = "大单净流入-净额")
    private BigDecimal largeNetInflow;

    @Column(name = "large_net_inflow_rate", precision = 5, scale = 2, comment = "大单净流入-净占比")
    private BigDecimal largeNetInflowRate;

    @Column(name = "medium_net_inflow", precision = 15, scale = 2, comment = "中单净流入-净额")
    private BigDecimal mediumNetInflow;

    @Column(name = "medium_net_inflow_rate", precision = 5, scale = 2, comment = "中单净流入-净占比")
    private BigDecimal mediumNetInflowRate;

    @Column(name = "small_net_inflow", precision = 15, scale = 2, comment = "小单净流入-净额")
    private BigDecimal smallNetInflow;

    @Column(name = "small_net_inflow_rate", precision = 5, scale = 2, comment = "小单净流入-净占比")
    private BigDecimal smallNetInflowRate;

}
