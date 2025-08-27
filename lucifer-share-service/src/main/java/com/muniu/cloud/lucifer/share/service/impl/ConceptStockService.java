package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.muniu.cloud.lucifer.share.service.entity.BoardStock;
import com.muniu.cloud.lucifer.share.service.mapper.ConceptStockMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 概念板块成份股关系服务
 */
@Service
@Slf4j
public class ConceptStockService extends ServiceImpl<ConceptStockMapper, BoardStock> {

    private final AkToolsService akToolsService;
    private final TradingDayService tradingDayService;
    private final TdConceptMarketService tdConceptMarketService;
    /**
     * Redis中存储概念板块更新日期的key
     */
    private static final String CONCEPT_STOCK_UPDATE_KEY = "share:concept:stock";

    private final RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    public ConceptStockService(AkToolsService akToolsService, TradingDayService tradingDayService, TdConceptMarketService tdConceptMarketService, RedisTemplate<String, Object> redisTemplate) {
        this.akToolsService = akToolsService;
        this.tradingDayService = tradingDayService;
        this.tdConceptMarketService = tdConceptMarketService;
        this.redisTemplate = redisTemplate;
    }
    
    
    /**
     * 每6秒执行一次同步，从凌晨1点到早上8点
     */
    @Scheduled(fixedRate = 6000)
    @Transactional
    public void scheduledSyncAllBoardStocks() {
        LocalTime now = LocalTime.now();
        if (tradingDayService.isTradingDay() && !(now.isAfter(LocalTime.of(1, 0)) && now.isBefore(LocalTime.of(8, 0)))) {
            return;
        }
        try {
            // 获取一个未更新的概念板块（更新日期不是今天的）
            List<String> boardCodes = tdConceptMarketService.getConceptBoardCodes();
            if(CollectionUtils.isEmpty(boardCodes)){
                return;
            }
            // 获取当前日期，格式为yyyyMMdd
            int today = Integer.parseInt(LocalDate.now().format(java.time.format.DateTimeFormatter.BASIC_ISO_DATE));
            // 从Redis中获取所有概念板块的更新状态
            HashOperations<String, String, Object> hashOps = redisTemplate.opsForHash();
            String boardCodeToUpdate = null;
            if(redisTemplate.hasKey(CONCEPT_STOCK_UPDATE_KEY)){
                Map<String,Object> boardCodesCache = hashOps.entries(CONCEPT_STOCK_UPDATE_KEY);
                for (String boardCode : boardCodes) {
                    if (!boardCodesCache.containsKey(boardCode)) {
                        boardCodeToUpdate = boardCode;
                        break;
                    }
                    int updateDate = (boardCodesCache.get(boardCode) != null) ? Integer.parseInt(boardCodesCache.get(boardCode).toString()) : 0;
                    if (updateDate != today) {
                        boardCodeToUpdate = boardCode;
                        break;
                    }
                }
            }else {
                boardCodeToUpdate = boardCodes.getFirst();
            }
            if(StringUtils.isBlank(boardCodeToUpdate)){
                return;
            }

            // 同步指定概念板块的成份股
            log.info("正在同步概念板块[{}]的成份股", boardCodeToUpdate);
            syncBoardStocks(boardCodeToUpdate);
            
            // 更新Redis中的记录
            hashOps.put(CONCEPT_STOCK_UPDATE_KEY, boardCodeToUpdate, String.valueOf(today));
            log.info("概念板块[{}]成份股同步成功，已更新Redis记录", boardCodeToUpdate);
            
        } catch (Exception e) {
            log.error("同步概念板块成份股失败: {}", e.getMessage(), e);
        }
    }

    
    /**
     * 同步指定概念板块的成份股
     * 
     * @param boardCode 概念板块代码
     * @throws IOException 请求异常
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncBoardStocks(String boardCode) throws IOException {
        log.info("开始同步概念板块[{}]成份股", boardCode);
        
        // 从API获取概念板块成份股
        String jsonData = akToolsService.stockBoardConceptConsEm(boardCode);
        if (StringUtils.isBlank(jsonData)) {
            log.warn("概念板块[{}]未获取到成份股数据", boardCode);
            return;
        }
        
        JSONArray array = JSON.parseArray(jsonData);
        if (array == null || array.isEmpty()) {
            log.warn("概念板块[{}]成份股数据为空", boardCode);
            return;
        }
        // 提取成份股代码
        List<String> currentStockCodes = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            String stockCode = item.getString("代码");
            if (StringUtils.isNotBlank(stockCode)) {
                currentStockCodes.add(stockCode);
            }
        }
        if (currentStockCodes.isEmpty()) {
            log.warn("概念板块[{}]解析成份股代码为空", boardCode);
            return;
        }
        
        // 查询数据库中该概念板块当前有效的成份股
        List<BoardStock> dbStocks = this.lambdaQuery().eq(BoardStock::getBoardCode, boardCode).eq(BoardStock::getIsValid, Boolean.TRUE).list();
        Set<String> dbStockCodes = dbStocks.stream().map(BoardStock::getStockCode).collect(Collectors.toSet());
        // 需要新增的成份股
        List<String> toAddStockCodes = currentStockCodes.stream().filter(code -> !dbStockCodes.contains(code)).toList();
        // 需要标记为无效的成份股
        List<String> toInvalidateStockCodes = dbStockCodes.stream().filter(code -> !currentStockCodes.contains(code)).collect(Collectors.toList());
        long currentTime = System.currentTimeMillis();
        // 1. 处理新增的成份股
        if (!toAddStockCodes.isEmpty()) {
            List<BoardStock> toAddStocks = toAddStockCodes.stream().map(stockCode -> new BoardStock(boardCode,stockCode,currentTime)).collect(Collectors.toList());
            getBaseMapper().batchInsert(toAddStocks);
            log.info("概念板块[{}]新增{}个成份股", boardCode, toAddStocks.size());
        }
        
        // 2. 处理需要标记为无效的成份股
        if (!toInvalidateStockCodes.isEmpty()) {
            this.lambdaUpdate().eq(BoardStock::getBoardCode, boardCode).in(BoardStock::getStockCode, toInvalidateStockCodes).set(BoardStock::getIsValid, Boolean.FALSE).set(BoardStock::getUpdateTime, currentTime).update();
            log.info("概念板块[{}]移除{}个成份股", boardCode, toInvalidateStockCodes.size());
        }
        log.info("概念板块[{}]成份股同步完成，当前有效成份股{}个", boardCode, currentStockCodes.size());
    }
    
    /**
     * 手动触发同步指定概念板块的成份股
     * 
     * @param boardCode 概念板块代码
     * @return 处理结果
     */
    @Transactional
    public String manualSyncBoardStocks(String boardCode) {
        if (StringUtils.isBlank(boardCode)) {
            return "概念板块代码不能为空";
        }
        
        try {
            syncBoardStocks(boardCode);
            return "概念板块[" + boardCode + "]成份股同步成功";
        } catch (Exception e) {
            log.error("手动同步概念板块[{}]成份股失败: {}", boardCode, e.getMessage(), e);
            return "同步失败: " + e.getMessage();
        }
    }
} 