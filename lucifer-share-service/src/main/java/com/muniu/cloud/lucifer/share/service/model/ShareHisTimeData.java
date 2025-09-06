package com.muniu.cloud.lucifer.share.service.model;

import com.muniu.cloud.lucifer.commons.model.dto.BaseModel;
import lombok.Data;

@Data
public class ShareHisTimeData extends BaseModel {

    /**
     * 股票代码
     * */
    private String shareCode;

    /**
     * 日期
     * */
    private int day;

    /**
     * 开盘价
     * */
    private double openPrice;

    /**
     * 收盘价
     * */
    private double closePrice;

    /**
     * 最高价
     * */
    private double highPrice;

    /**
     * 最低价
     * */
    private double lowPrice;

    /**
     * 成交量
     * */
    private double volume;

    /**
     * 成交额
     * */
    private double amount;

    /**
     * 振幅
     * */
    private double amplitude;

    /**
     * 涨跌幅
     * */
    private double changeRate;

    /**
     * 涨跌额
     * */
    private double changeAmount;

    /**
     * 换手率
     * */
    private double turnoverRate;

    /**
     * 昨收价
     * */
    private double preClosePrice;

    /**
     * 涨停价
     * */
    private double upLimitPrice;

    /**
     * 跌停价
     * */
    private double downLimitPrice;
}
