package com.muniu.cloud.lucifer.share.service.impl;

import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.share.service.entity.TradingDay;
import com.muniu.cloud.lucifer.share.service.mapper.TradingDayMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * 交易日服务
 */
@Service
@Slf4j
public class TradingDayService {
    
    /**
     * 交易日有序集合缓存，使用TreeSet保证有序性且支持快速查找前后节点
     */
    private final NavigableSet<Integer> tradingDaysCache = new TreeSet<>();

    private final TradingDayMapper tradingDayMapper;

    private final AkToolsService akToolsService;


    /**
     * 交易时间常量定义
     */
    private static final LocalTime BIDDING_START = LocalTime.of(9, 15);
    private static final LocalTime BIDDING_END = LocalTime.of(9, 25);
    private static final LocalTime MORNING_START = LocalTime.of(9, 15);
    private static final LocalTime MORNING_END = LocalTime.of(11, 30);
    private static final LocalTime AFTERNOON_START = LocalTime.of(13, 0);
    private static final LocalTime AFTERNOON_END = LocalTime.of(15, 1);

    public TradingDayService(AkToolsService akToolsService, TradingDayMapper tradingDayMapper) {
        this.akToolsService = akToolsService;
        this.tradingDayMapper = tradingDayMapper;
    }

    /**
     * 应用启动时初始化交易日数据
     */
    @PostConstruct
    public void init() {
        if (CollectionUtils.isEmpty(getTradingDaysSet())) {
            log.info("交易日数据为空");
        }
    }

    /**
     * 每天中午12点同步交易日数据
     */
    @Scheduled(cron = "0 0 12 * * ?")
    public void scheduledSyncTradingDays() {
        log.info("定时任务：开始同步交易日数据");
        try {
            List<Integer> tradingDays = akToolsService.toolTradeDateHistSina();
            if (CollectionUtils.isEmpty(tradingDays)) {
                log.warn("从远程接口获取交易日数据为空");
                return;
            }
            if(CollectionUtils.isNotEmpty(tradingDaysCache)){
                tradingDays.removeAll(tradingDaysCache);
            }
            if (CollectionUtils.isEmpty(tradingDays)) {
                log.info("日期数据无变化，不用更新");
                return;
            }
            log.info("获取到{}个交易日数据", tradingDays.size());
            saveToDatabase(tradingDays);
        } catch (Exception e) {
            log.error("同步交易日数据出现异常: {}", e.getMessage(), e);
        }
    }


    /**
     * 将交易日数据保存到数据库
     */
    private void saveToDatabase(List<Integer> tradingDays) {
        if (CollectionUtils.isEmpty(tradingDays)) {
            return;
        }

        long now = System.currentTimeMillis();
        List<TradingDay> entities = tradingDays.stream().map(e -> new TradingDay(e, now, now)).toList();
        if (CollectionUtils.isEmpty(entities)) {
            return;
        }
        tradingDayMapper.batchInsert(entities);
        log.info("成功保存{}个交易日数据到数据库", entities.size());
    }

    /**
     * 获取指定日期范围内的所有交易日
     * 
     * @param startDate 开始日期，格式：yyyyMMdd
     * @param endDate 结束日期，格式：yyyyMMdd
     * @return 交易日列表
     */
    public List<Integer> getTradingDaysBetween(int startDate, int endDate) {
        NavigableSet<Integer> allDays = getTradingDaysSet();
        
        if (CollectionUtils.isEmpty(allDays)) {
            return Lists.newArrayList();
        }

        return new ArrayList<>(allDays.subSet(startDate, true, endDate, true));
    }

    /**
     * 获取交易日有序集合
     * 
     * @return 交易日有序集合
     */
    private NavigableSet<Integer> getTradingDaysSet() {
        if (CollectionUtils.isNotEmpty(tradingDaysCache)) {
            return tradingDaysCache;
        }
        
        synchronized (this) {
            if (CollectionUtils.isNotEmpty(tradingDaysCache)) {
                return tradingDaysCache;
            }
            List<Integer> days = tradingDayMapper.getAllTradingDays();

            if (CollectionUtils.isNotEmpty(days)) {
                tradingDaysCache.addAll(days);
                log.info("已加载{} - {} 交易日", tradingDaysCache.pollFirst(),tradingDaysCache.last());
                return tradingDaysCache;
            }

            log.info("数据库中无交易日数据，尝试从远程接口获取");
            try {
                days = akToolsService.toolTradeDateHistSina();
                if (CollectionUtils.isNotEmpty(days)) {
                    saveToDatabase(days);
                    tradingDaysCache.addAll(days);
                } else {
                    log.warn("远程接口未返回交易日数据");
                }
            } catch (Exception e) {
                log.error("从远程接口获取交易日数据失败: {}", e.getMessage(), e);
            }

        }
        
        return tradingDaysCache;
    }

    /**
     * 获取所有交易日
     *
     * @return 所有交易日列表
     */
    public List<Integer> getAllTradingDays() {
        return new ArrayList<>(getTradingDaysSet());
    }

    /**
     * 判断指定日期是否为交易日
     * 
     * @param date 日期，格式：yyyyMMdd
     * @return 是否为交易日
     */
    public boolean isTradingDay(int date) {
        NavigableSet<Integer> allTradingDays = getTradingDaysSet();
        return allTradingDays.contains(date);
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
        int dateInt = Integer.parseInt(date.format(DateTimeFormatter.BASIC_ISO_DATE));
        return isTradingDay(dateInt);
    }

    /**
     * 获取指定日期之后的第一个交易日
     * 
     * @param date 日期，格式：yyyyMMdd
     * @return 下一个交易日，如果没有找到返回-1
     */
    public int getNextTradingDay(int date) {
        // 直接获取大于指定日期的最小元素
        Integer nextDay = getTradingDaysSet().higher(date);
        return Objects.nonNull(nextDay) ? nextDay : -1;
    }

    /**
     * 获取指定日期之前的第一个交易日
     * 
     * @param date 日期，格式：yyyyMMdd
     * @return 上一个交易日，如果没有找到返回-1
     */
    public int getPreviousTradingDay(int date) {
        NavigableSet<Integer> allTradingDays = getTradingDaysSet();
        
        // 直接获取小于等于指定日期的最大元素
        Integer prevDay = allTradingDays.floor(date);
        if (Objects.nonNull(prevDay) && prevDay < date) {
            return prevDay;
        }
        // 如果当天是交易日，获取严格小于的最大元素
        Integer strictPrevDay = allTradingDays.lower(date);
        return Objects.nonNull(strictPrevDay) ? strictPrevDay : -1;
    }

    /**
     *
     * */
    public int getLstTradingDay() {
        int dateInt = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        Integer day = tradingDaysCache.floor(dateInt);
        return day == null ? 1 : day;
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
            return date;
        }
        
        // 从交易日缓存中直接获取数据
        NavigableSet<Integer> tradingDaysSet = getTradingDaysSet();
        if (CollectionUtils.isEmpty(tradingDaysSet)) {
            log.error("交易日历数据为空，无法获取前{}个交易日", off);
            return -1;
        }
        
        // 找到小于等于当前日期的最大交易日
        Integer currentOrPreviousDay = tradingDaysSet.floor(date);
        if (currentOrPreviousDay == null) {
            log.warn("未找到小于等于{}的交易日", date);
            return -1;
        }
        
        // 向前查找off个交易日
        Integer targetDay = currentOrPreviousDay;
        int count = 0;
        
        while (count < off && targetDay != null) {
            targetDay = tradingDaysSet.lower(targetDay);
            count++;
        }
        
        if (targetDay == null) {
            log.warn("交易日历数据不足，无法获取前{}个交易日，当前只能获取{}个", off, count);
            return -1;
        }
        
        return targetDay;
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
            return date;
        }
        
        // 从交易日缓存中直接获取数据
        NavigableSet<Integer> tradingDaysSet = getTradingDaysSet();
        if (CollectionUtils.isEmpty(tradingDaysSet)) {
            log.error("交易日历数据为空，无法获取后{}个交易日", off);
            return -1;
        }
        
        // 找到大于等于当前日期的最小交易日
        Integer currentOrNextDay = tradingDaysSet.ceiling(date);
        if (currentOrNextDay == null) {
            log.warn("未找到大于等于{}的交易日", date);
            return -1;
        }
        
        // 向后查找off个交易日
        Integer targetDay = currentOrNextDay;
        int count = 0;
        
        while (count < off && targetDay != null) {
            targetDay = tradingDaysSet.higher(targetDay);
            count++;
        }
        
        if (targetDay == null) {
            log.warn("交易日历数据不足，无法获取后{}个交易日，当前只能获取{}个", off, count);
            return -1;
        }
        
        return targetDay;
    }


    
    /**
     * 判断当前是否是交易日的交易时间
     * 交易时间为：9:15-11:30, 13:00-15:00
     * 
     * @return 是否在交易时间内
     */
    public boolean isTradingTime(boolean bidding) {
        // 先判断当天是否为交易日
        if (!isTradingDay(LocalDate.now())) {
            return false;
        }
        // 再判断当前时间是否在交易时间段内
        LocalTime now = LocalTime.now();
        return (bidding && now.isAfter(BIDDING_START) && now.isBefore(BIDDING_END)) || (now.isAfter(MORNING_START) && now.isBefore(MORNING_END) || now.isAfter(AFTERNOON_START) && now.isBefore(AFTERNOON_END));
    }

    /**
     * 判断当前是否是交易日的交易时间
     * 交易时间为：9:15-11:30, 13:00-15:00
     *
     * @return 是否在交易时间内
     */
    public boolean isNotTradingTime() {
        // 先判断当天是否为交易日
        if (!isTradingDay(LocalDate.now())) {
            return false;
        }
        // 再判断当前时间是否在交易时间段内
        LocalTime now = LocalTime.now();
        return  !(now.isAfter(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(17, 0)));
    }

    public static void main(String[] args) {
        LocalTime now = LocalTime.now();
        boolean a=  !(now.isAfter(LocalTime.of(9, 0)) && now.isBefore(LocalTime.of(17, 0)));
        System.out.println(a);
    }
} 