package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingService;
import com.muniu.cloud.lucifer.share.service.config.ScheduledInterface;
import com.muniu.cloud.lucifer.share.service.entity.ConceptMarket;
import com.muniu.cloud.lucifer.share.service.mapper.TdConceptMarketMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 概念板块服务实现类
 */
@Service
@Slf4j
public class TdConceptMarketService extends BaseShardingService<TdConceptMarketMapper,ConceptMarket> implements ScheduledInterface {
    /**
     * 概念板块市场数据Redis缓存key前缀
     */
    private static final String CONCEPT_BOARD_MARKET_KEY_PREFIX = "board:market:";


    private static final List<String> CONCEPT_BOARD_CODES = Lists.newCopyOnWriteArrayList();
    /**
     * 月份格式化器
     */
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMM");
    
    /**
     * 缓存锁，用于确保并发环境下只加载一次表缓存
     */
    private final Object cacheLock = new Object();

    private final AkToolsService akToolsService;

    private final RedisTemplate<String, Object> redisTemplate;
    private final TradingDateTimeService tradingDayService;
    
    @Autowired
    public TdConceptMarketService(RedisTemplate<String, Object> redisTemplate, TradingDateTimeService tradingDayService, AkToolsService akToolsService) {
        this.redisTemplate = redisTemplate;
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }
    


    
    /**
     * 获取当前月份
     * @return 当前月份，格式为yyyyMM
     */
    public static String getCurrentMonth() {
        return getMonth(LocalDate.now());
    }
    
    /**
     * 获取指定日期的月份
     * @param date 日期
     * @return 月份，格式为yyyyMM
     */
    public static String getMonth(LocalDate date) {
        return date.format(MONTH_FORMATTER);
    }

    public List<String> getConceptBoardCodes(){
        if(CollectionUtils.isEmpty(CONCEPT_BOARD_CODES)){
            synchronized (cacheLock){
                if(CollectionUtils.isEmpty(CONCEPT_BOARD_CODES)){
                    try {
                        List<ConceptMarket> boardList = akToolsService.stockBoardConceptNameEm();
                        updateConceptBoardCodes(boardList);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
        return CONCEPT_BOARD_CODES;
    }

    private void updateConceptBoardCodes(List<ConceptMarket> conceptMarkets) {
        if (CollectionUtils.isEmpty(conceptMarkets)) {
            return;
        }
        Set<String> newCodes = conceptMarkets.stream().map(ConceptMarket::getBoardCode).collect(Collectors.toSet());
        CONCEPT_BOARD_CODES.stream().filter(e-> !newCodes.contains(e)).forEach(CONCEPT_BOARD_CODES::remove);
        CONCEPT_BOARD_CODES.forEach(newCodes::remove);
        CONCEPT_BOARD_CODES.addAll(newCodes);
    }

    /**
     * 同步概念板块数据
     */
    @Transactional(rollbackFor = Exception.class)
    public void scheduled() throws Exception {
        if(!tradingDayService.isTradingTime(true)){
            return;
        }
        log.info("开始同步概念板块数据");
        // 获取概念板块数据
        List<ConceptMarket> boardList = akToolsService.stockBoardConceptNameEm();
        if (CollectionUtils.isEmpty(boardList)) {
            log.warn("未获取到概念板块数据");
            return;
        }
        // 保存数据到数据库
        saveConceptBoardData(boardList);
        updateConceptBoardCodes(boardList);
        updateMarketDataCache(boardList);
        log.info("同步概念板块数据完成，共{}条记录", boardList.size());
    }

    /**
     * 保存概念板块数据到数据库
     * @param boardList 概念板块数据列表
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveConceptBoardData(List<ConceptMarket> boardList) {
        if (CollectionUtils.isEmpty(boardList)) {
            return;
        }
        String month = getCurrentMonth();
        try {
            // 确保表存在
            createTable("td_concept_market_", month);
            // 批量插入或更新数据
            log.info("开始批量保存概念板块数据，数据量：{}，月份：{}", boardList.size(), month);
            getBaseMapper().batchInsertOrUpdate(boardList, month);
            log.info("批量保存概念板块数据完成");
        } catch (Exception e) {
            log.error("批量保存概念板块数据失败", e);
            throw e;
        }
    }

    /**
     * 获取所有概念板块数据
     * @return 概念板块数据列表
     */
    @Transactional
    public List<String> getAllConceptBoards() {
        return CONCEPT_BOARD_CODES;
    }
    

    
    /**
     * 更新市场数据缓存，每个概念板块使用单独的key
     * @param boardList 概念板块数据列表
     */
    private void updateMarketDataCache(List<ConceptMarket> boardList) {
        if (CollectionUtils.isEmpty(boardList)) {
            return;
        }
        try {
            // 获取当前日期（格式：yyyyMMdd）
            int today = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
            // 计算过期时间：下一个交易日的早上8点
            long expirationTime = calculateExpirationTime();
            // 按概念板块代码分组存储
            for (ConceptMarket board : boardList) {
                if (StringUtils.isBlank(board.getBoardCode())) {
                    continue;
                }
                // 生成Redis key: board:market:{boardCode}:{day}
                String redisKey = CONCEPT_BOARD_MARKET_KEY_PREFIX + board.getBoardCode() + ":" + today;
                // 检查key是否已存在，不存在则设置过期时间
                Boolean hasKey = redisTemplate.hasKey(redisKey);
                // 将数据添加到集合中
                redisTemplate.opsForSet().add(redisKey, board);
                // 如果是新建的key，设置过期时间
                if (!hasKey) {
                    redisTemplate.expire(redisKey, expirationTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
                }
            }
            log.info("更新概念板块市场数据缓存成功，当前数量: {}", boardList.size());
        } catch (Exception e) {
            log.error("更新概念板块市场数据缓存失败", e);
        }
    }
    
    /**
     * 计算缓存过期时间（下一个交易日的早上8点）
     * @return 过期时间的时间戳（毫秒）
     */
    private long calculateExpirationTime() {
        LocalDate today = LocalDate.now();
        int todayInt = Integer.parseInt(today.format(DateTimeFormatter.BASIC_ISO_DATE));
        // 获取下一个交易日
        int nextTradingDay = tradingDayService.getNextTradingDay(todayInt);
        // 如果获取失败，默认设置为明天早上8点
        if (nextTradingDay == -1) {
            return today.plusDays(1).atTime(8, 0, 0).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
        }
        // 解析下一个交易日
        LocalDate nextTradingDate = LocalDate.parse(String.valueOf(nextTradingDay), DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        // 设置为下一个交易日的早上8点
        return nextTradingDate.atTime(8, 0, 0).atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
    }
    
    /**
     * 获取指定概念板块的当日市场数据
     * @param boardCode 概念板块代码
     * @return 当日市场数据列表
     */
    public List<ConceptMarket> getBoardMarketData(String boardCode) {
        if (StringUtils.isBlank(boardCode)) {
            return new ArrayList<>();
        }
        // 获取当前日期（格式：yyyyMMdd）
        int today = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        // 生成Redis key
        String redisKey = CONCEPT_BOARD_MARKET_KEY_PREFIX + boardCode + ":" + today;
        // 从Redis获取数据
        Set<Object> dataSet = redisTemplate.opsForSet().members(redisKey);
        if (dataSet != null && !dataSet.isEmpty()) {
            // 转换为列表
            return dataSet.stream()
                    .map(obj -> JSON.parseObject(JSON.toJSONString(obj), ConceptMarket.class))
                    .collect(Collectors.toList());
        }
        // 如果缓存中没有数据，可以考虑从数据库加载
        // 这里简化处理，返回空列表
        return new ArrayList<>();
    }
    
    /**
     * 根据概念板块代码和日期查询实时数据
     * 
     * @param boardCode 概念板块代码
     * @param date 日期，格式为yyyyMMdd的整数，如20210101
     * @return 概念板块实时数据列表
     */
    @Transactional(rollbackFor = Exception.class)
    public List<ConceptMarket> getRealtimeDataByCodeAndDate(String boardCode, Integer date) {
        if (StringUtils.isBlank(boardCode) || date == null) {
            return new ArrayList<>();
        }
        try {
            // 首先尝试从Redis缓存获取
            String redisKey = CONCEPT_BOARD_MARKET_KEY_PREFIX + boardCode + ":" + date;
            Set<Object> dataSet = redisTemplate.opsForSet().members(redisKey);
            if (dataSet != null && !dataSet.isEmpty()) {
                // 转换为列表并按更新时间排序
                return dataSet.stream()
                    .map(obj -> JSON.parseObject(JSON.toJSONString(obj), ConceptMarket.class))
                    .sorted(Comparator.comparing(ConceptMarket::getUpdateTime))
                    .collect(Collectors.toList());
            }
            // 缓存未命中，从数据库查询
            // 解析日期
            LocalDate parsedDate = LocalDate.parse(String.valueOf(date), DateTimeFormatter.ofPattern("yyyyMMdd"));
            // 获取月份
            String month = getMonth(parsedDate);
            // 确保分表存在
            createTable("td_concept_market_",month);
            // 计算当天起始时间和结束时间（毫秒时间戳）
            long startTime = parsedDate.atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endTime = parsedDate.plusDays(1).atStartOfDay().atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli() - 1;
            // 调用Mapper查询
            return getBaseMapper().getRealtimeDataByCodeAndDate(boardCode, startTime, endTime, month);
        } catch (Exception e) {
            log.error("根据概念板块代码和日期查询实时数据失败: {}, {}", boardCode, date, e);
            return new ArrayList<>();
        }
    }
} 