package com.muniu.cloud.lucifer.share.service.config;

import com.muniu.cloud.lucifer.commons.core.utls.SpringContextUtils;
import com.muniu.cloud.lucifer.share.service.clients.SinaShareMarketApiClient;
import com.muniu.cloud.lucifer.share.service.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalTime;

@Component
public class ScheduledConfig {

    private final TradingDateTimeService tradingDayService;

    @Autowired
    public ScheduledConfig(TradingDateTimeService tradingDayService) {
        this.tradingDayService = tradingDayService;
    }


    boolean isDebug = true;

    /**
     * 获取实时股票数据（当前所有A股数据）
     * 交易时段运行
     * */
    @Scheduled(cron = "0 * * * * MON-FRI")
//    @Scheduled(cron = "0 * * * * *")
    public void sinaShareMarket() throws Exception{
        if(!tradingDayService.isTradingTime()){
            return;
        }
        SpringContextUtils.getBean(SinaShareMarketApiClient.class).scheduled();
    }




    /**
     * 获取股票交易数据
     * 非交易时间运行
     * */
    @Scheduled(cron = "*/5 * * * * *")
    public void stockRealTime() throws Exception{
        if(isDebug || tradingDayService.isTradingTime()){
            return;
        }
        SpringContextUtils.getBean(TdStockRealTimeService.class).scheduled();
    }


    /**
     * 获取股票历史数据
     * 非交易时间运行
     * */
    @Scheduled(cron = "*/6 * * * * *")
    public void shareHist() throws Exception {
        LocalTime now = LocalTime.now();
//        if (tradingDayService.isTradingDay(LocalDate.now()) && (now.isAfter(LocalTime.of(9, 15)) && now.isBefore(LocalTime.of(15, 30)))) {
//            return;
//        }
        SpringContextUtils.getBean(TdShareHistService.class).scheduled();

    }

    /**
     * 同步概念板块数据
     * */
    @Scheduled(cron = "0 * * * * MON-FRI")
    public void sonceptMarket() throws Exception {
        if(isDebug){
            return;
        }
        if(!tradingDayService.isTradingTime()){
            return;
        }
        SpringContextUtils.getBean(TdConceptMarketService.class).scheduled();
    }


    /**
     * 同步概念板块成分股
     * 每6秒执行一次同步，从凌晨1点到早上8点
     * */
    @Scheduled(fixedRate = 6000)
    public void conceptStock() throws Exception {
        if(isDebug){
            return;
        }
        if(!tradingDayService.isTradingTime()){
            return;
        }
        SpringContextUtils.getBean(ConceptStockService.class).scheduled();
    }
}


