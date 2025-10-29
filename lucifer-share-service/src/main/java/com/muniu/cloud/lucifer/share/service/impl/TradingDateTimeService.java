package com.muniu.cloud.lucifer.share.service.impl;

import com.muniu.cloud.lucifer.commons.core.redis.IntegerRedisTemplate;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import com.muniu.cloud.lucifer.share.service.constant.LuciferShareConstant;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 交易日服务
 * @author antaohua
 */
@Service
@Slf4j
public class TradingDateTimeService {

    private final IntegerRedisTemplate redisTemplate;

    private final AkToolsService akToolsService;



    public TradingDateTimeService(AkToolsService akToolsService, IntegerRedisTemplate redisTemplate) {
        this.akToolsService = akToolsService;
        this.redisTemplate = redisTemplate;
    }



    /**
     * 每天时检查一次获取交易日数据
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void syncTradingDays() {
        log.info("定时任务：开始同步交易日数据");
        if (redisTemplate.hasKey(LuciferShareConstant.TRADING_TIME_KEY)) {
            Set<Integer> result = redisTemplate.opsForZSet().reverseRangeByScore(LuciferShareConstant.TRADING_TIME_KEY, Double.NEGATIVE_INFINITY, Double.MAX_VALUE, 0, 1);
            if (result != null && !result.isEmpty() && result.iterator().next() > Integer.parseInt(LocalDate.now().format(DateConstant.DATE_FORMATTER_YYYYMMDD))) {
                return;
            }
        }
        try {
            List<Integer> tradingDays = akToolsService.toolTradeDateHistSina();
            Set<ZSetOperations.TypedTuple<Integer>> tuples = tradingDays.stream().map(e -> new DefaultTypedTuple<>(e, e.doubleValue())).collect(Collectors.toSet());
            redisTemplate.opsForZSet().addIfAbsent(LuciferShareConstant.TRADING_TIME_KEY, tuples);
        }catch (IOException e){
            log.error(e.getMessage());
        }
    }

    @PostConstruct
    public void initLastTradingData(){
        updatedCurrentTradingDay();
    }

    /**
     * 更新当前交易日
     */
    @Scheduled(cron = "0 * * * * ?")
    public void updatedCurrentTradingDay() {
        if(isTradingDay() && LocalTime.now().isAfter(LuciferShareConstant.TRADING_TIME_START)){
            LuciferShareConstant.LAST_TRADING_DATA = LocalDate.now();
        }else {
            int tradingDay = getPreviousTradingDay(Integer.parseInt(LocalDate.now().format(DateConstant.DATE_FORMATTER_YYYYMMDD)));
            if(tradingDay > 0){
                String dateStr = String.valueOf(tradingDay);
                LuciferShareConstant.LAST_TRADING_DATA = LocalDate.parse(dateStr, DateConstant.DATE_FORMATTER_YYYYMMDD);
            }
        }





    }


    /**
     * 获取指定日期范围内的所有交易日
     * 如果 startDate和endDate 是交易日则包含(startDate和endDate）
     *
     * @param startDate 开始日期，格式：yyyyMMdd
     * @param endDate   结束日期，格式：yyyyMMdd
     * @return 交易日列表
     */
    public List<Integer> getTradingDaysBetween(int startDate, int endDate) {
        // 获取范围内的元素，按 score 排序
        Set<Integer> tradingDays = redisTemplate.opsForZSet()
                .rangeByScore(LuciferShareConstant.TRADING_TIME_KEY, startDate, endDate);

        if (tradingDays == null || tradingDays.isEmpty()) {
            syncTradingDays();
            return List.of();
        }
        // 转成 List 返回
        return tradingDays.stream().sorted().collect(Collectors.toList());
    }

    /**
     * 判断指定日期是否为交易日
     * 
     * @param date 日期，格式：yyyyMMdd
     * @return 是否为交易日
     */
    public boolean isTradingDay(int date) {
        Double score = redisTemplate.opsForZSet()
                .score(LuciferShareConstant.TRADING_TIME_KEY, date);
        return score != null;
    }

    /***
     * 判断当前日期是否为交易日
     * @return 是否为交易日
     */
    public boolean isTradingDay() {
        return isTradingDay(LocalDate.now());
    }
    /**
     * 判断指定日期是否为交易日
     * 
     * @param date 日期
     * @return 是否为交易日
     */
    public boolean isTradingDay(LocalDate date) {
        if (Objects.isNull(date)) {
            return false;
        }
        int dateInt = Integer.parseInt(date.format(DateConstant.DATE_FORMATTER_YYYYMMDD));
        return isTradingDay(dateInt);
    }

    /**
     * 获取指定日期之后的第一个交易日
     * 
     * @param date 日期，格式：yyyyMMdd
     * @return 下一个交易日，如果没有找到返回-1
     */
    public int getNextTradingDay(int date) {
        // 使用 ZSet 的 rangeByScore 获取大于指定日期的最小值
        Set<Integer> result = redisTemplate.opsForZSet().rangeByScore(LuciferShareConstant.TRADING_TIME_KEY, date + 1, Double.MAX_VALUE, 0, 1);

        if (result == null || result.isEmpty()) {
            syncTradingDays();
            return -1;
        }
        // 取第一个元素
        return result.iterator().next();
    }

    /**
     * 获取指定日期之前的第一个交易日
     * 
     * @param date 日期，格式：yyyyMMdd
     * @return 上一个交易日，如果没有找到返回-1
     */
    public int getPreviousTradingDay(int date) {
        // 使用 ZSet 的 reverseRangeByScore 获取小于指定日期的最大值
        Set<Integer> result = redisTemplate.opsForZSet()
                .reverseRangeByScore(LuciferShareConstant.TRADING_TIME_KEY, Double.NEGATIVE_INFINITY, date - 1, 0, 1);

        if (result == null || result.isEmpty()) {
            syncTradingDays();
            return -1;
        }
        // 取第一个元素，即离 date 最近的上一个交易日
        return result.iterator().next();
    }

    /**
     * 获取最近一个交易日
     * */
    public int getLstTradingDay() {
        return Integer.parseInt(LuciferShareConstant.LAST_TRADING_DATA.format(DateConstant.DATE_FORMATTER_YYYYMMDD));
    }

    /**
     * 获取指定日期之前的第N个交易日
     *
     * @param date 日期，格式：yyyyMMdd
     * @param off 前几日
     * @return 上一个交易日，如果没有找到返回-1
     */
    public int getPreviousTradingDay(int date, int off) {
        if (off <= 0) {
            throw new IllegalArgumentException("off 必须大于0");
        }

        Set<Integer> result = redisTemplate.opsForZSet()
                .reverseRangeByScore(LuciferShareConstant.TRADING_TIME_KEY, Double.NEGATIVE_INFINITY, date - 1, off - 1, 1);

        if (result == null || result.isEmpty()) {
            syncTradingDays();
            return -1;
        }

        return result.iterator().next();
    }


    /**
     * 获取指定日期之前的第N个交易日（不包含传入日期）
     *
     * @param date 日期，格式：yyyyMMdd
     * @param off  前几日
     * @return 日期列表，按时间升序排列
     */
    public List<Integer> getPreviousTradingDays(int date, int off) {
        if (off <= 0) {
            throw new IllegalArgumentException("off 必须大于0");
        }
        // 从倒序结果取前 off 个交易日
        Set<Integer> resultSet = redisTemplate.opsForZSet()
                .reverseRangeByScore(LuciferShareConstant.TRADING_TIME_KEY, Double.NEGATIVE_INFINITY, date - 1, 0, off);

        if (resultSet == null || resultSet.isEmpty()) {
            syncTradingDays();
            return List.of();
        }
        // 倒序转升序
        return resultSet.stream().sorted().collect(Collectors.toList());
    }



    /**
     * 获取指定日期之后的第N个交易日
     *
     * @param date 日期，格式：yyyyMMdd
     * @param off 后几日
     * @return 后第N个交易日，如果没有找到返回-1
     */
    public int getNextTradingDay(int date, int off) {
        if (off <= 0) {
            throw new IllegalArgumentException("off 必须大于0");
        }

        // 从指定日期之后开始取，第 off 个交易日
        Set<Integer> result = redisTemplate.opsForZSet()
                .rangeByScore(LuciferShareConstant.TRADING_TIME_KEY, date + 1, Double.MAX_VALUE, off - 1, 1);

        if (result == null || result.isEmpty()) {
            syncTradingDays();
            return -1;
        }

        return result.iterator().next();
    }

    public boolean isTradingData(int date) {
        Double score = redisTemplate.opsForZSet().score(LuciferShareConstant.TRADING_TIME_KEY, date);
        return score != null;
    }
    public boolean isBidding(){
        return isBidding( LocalTime.now());
    }
    public boolean isBidding(LocalTime now){
        return now.isAfter(LuciferShareConstant.BIDDING_START) && now.isBefore(LuciferShareConstant.BIDDING_END);
    }

    public boolean isMorning(){
        return isMorning(LocalTime.now());
    }
    public boolean isMorning(LocalTime now){
        return now.isAfter(LuciferShareConstant.MORNING_START) && now.isBefore(LuciferShareConstant.MORNING_END);
    }

    public boolean isAfternoon(){

        return isAfternoon(LocalTime.now());
    }
    public boolean isAfternoon(LocalTime now){
        return now.isAfter(LuciferShareConstant.AFTERNOON_START) && now.isBefore(LuciferShareConstant.AFTERNOON_END);
    }
    /**
     * 判断当前是否是交易日的交易时间
     * 交易时间为：9:10-16:00
     * 前后都留下一些冗余
     * @return 是否在交易时间内
     */
    public boolean isTradingTime() {
        LocalTime now = LocalTime.now();
        return now.isAfter(LocalTime.of(9, 10)) && now.isBefore(LocalTime.of(16, 0));
    }

} 