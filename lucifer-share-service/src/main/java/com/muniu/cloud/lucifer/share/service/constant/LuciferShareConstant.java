package com.muniu.cloud.lucifer.share.service.constant;

import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;

import java.time.LocalDate;
import java.time.LocalTime;

public class LuciferShareConstant {

    public static volatile LocalDate LAST_TRADING_DATA = LocalDate.of(2025, 9, 30);

    public static final LocalTime TRADING_TIME_START = LocalTime.of(9, 10);


    /**
     * 交易时间常量定义
     */
    public static final LocalTime BIDDING_START    = LocalTime.of(9, 15);
    public static final LocalTime BIDDING_END      = LocalTime.of(9, 26);
    public static final LocalTime MORNING_START    = LocalTime.of(9, 30);
    public static final LocalTime MORNING_END      = LocalTime.of(11, 31);
    public static final LocalTime AFTERNOON_START  = LocalTime.of(13, 0);
    public static final LocalTime AFTERNOON_END    = LocalTime.of(15, 1);

    public static final String TRADING_TIME_KEY = "DATE:TRADING_DAY";

    private static final String REDIS_SHARE_STATUS = "SHARE:DATE:STATUS:";


    private static final String REDIS_SHARE_SET = "SHARE:DATE:SET:";
    /**
     * Redis中存储概念板块更新日期的key
     */
    public static final String CONCEPT_STOCK_UPDATE_KEY = "SHARE:CONCEPT:STOCK";
    /**
     * Redis中存储股票市场行情数据的消息队列key
     */
    public static final String REDIS_STOCK_MARKET = "MQ:STOCK:MARKET";




    public static String getRedisShareStatusKey(String code) {
        String dayString = LAST_TRADING_DATA.format(DateConstant.DATE_FORMATTER_YYYYMMDD);

        return  REDIS_SHARE_STATUS + code + ":" + dayString;
    }

    public static String getRedisShareSetKey() {
        String dayString = LAST_TRADING_DATA.format(DateConstant.DATE_FORMATTER_YYYYMMDD);
        return REDIS_SHARE_SET + dayString;
    }



}
