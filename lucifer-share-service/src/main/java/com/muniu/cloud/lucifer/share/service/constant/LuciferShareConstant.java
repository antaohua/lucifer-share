package com.muniu.cloud.lucifer.share.service.constant;

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

    public static final String TRADING_TIME_KEY = "trading_time";

}
