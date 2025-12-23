package com.muniu.cloud.lucifer.share.service.model.dto;

import com.muniu.cloud.lucifer.commons.model.dto.BaseModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @author antaohua
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SinaStockMarketSaveEvent extends BaseModel {



//    {
//                        "symbol" : "bj920252",
//                            "code" : "920252",
//                            "name" : "天宏锂电",
//                            "trade" : "33.630",
//                            "pricechange" : 2.97,
//                            "changepercent" : 9.687,
//                            "buy" : "33.620",
//                            "sell" : "33.630",
//                            "settlement" : "30.660",
//                            "open" : "30.500",
//                            "high" : "35.000",
//                            "low" : "30.340",
//                            "volume" : 9787185,
//                            "amount" : 317853530,
//                            "ticktime" : "13:37:57",
//                            "per" : 480.429,
//                            "pb" : 13.223,
//                            "mktcap" : 345214.186395,
//                            "nmc" : 229461.656757,
//                            "turnoverratio" : 14.34414
//                    }

    /** 股票标识符（如 bj833346） */
    private String symbol;

    /** 股票代码（如 833346） */
    private String code;

    /** 股票名称 */
    private String name;

    /** 当前价格（成交价） */
    private String trade;

    /** 涨跌额（相较于昨收） */
    private double pricechange;

    /** 涨跌幅（%） */
    private double changepercent;

    /** 买一价 */
    private String buy;

    /** 卖一价 */
    private String sell;

    /** 昨日收盘价 */
    private String settlement;

    /** 今日开盘价 */
    private String open;

    /** 今日最高价 */
    private String high;

    /** 今日最低价 */
    private String low;

    /** 成交量（手） */
    private long volume;

    /** 成交金额（元） */
    private long amount;

    /** 最新成交时间（如 "15:00:03"） */
    private String ticktime;

    /** 市盈率（Price Earnings Ratio） */
    private double per;

    /** 市净率（Price to Book） */
    private double pb;

    /** 总市值（Market Capitalization，元） */
    private double mktcap;

    /** 流通市值（Net Market Capitalization，元） */
    private double nmc;

    /** 换手率（%） */
    private double turnoverratio;

    /** 数据加载时间（系统时间戳，毫秒） */
    private long loadTime;



    /**
     * 计算振幅（百分比）
     * @return 振幅，单位 %
     */
    public BigDecimal getAmplitude() {
        try {
            BigDecimal highVal = new BigDecimal(this.high);
            BigDecimal lowVal = new BigDecimal(this.low);
            BigDecimal settlementVal = new BigDecimal(this.settlement);

            if (settlementVal.compareTo(BigDecimal.ZERO) <= 0) {
                return BigDecimal.ZERO;
            }
            return highVal.subtract(lowVal).divide(settlementVal, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
        } catch (NumberFormatException | NullPointerException e) {
            return BigDecimal.ZERO;
        }
    }
}
