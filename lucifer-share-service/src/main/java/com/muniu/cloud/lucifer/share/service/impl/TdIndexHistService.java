package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.rest.exception.ResponseException;
import com.muniu.cloud.lucifer.share.service.model.cache.IndexInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.entity.TdIndexHist;
import com.muniu.cloud.lucifer.share.service.mapper.TdIndexHistMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 指数历史行情服务
 */
@Service
@Slf4j
public class TdIndexHistService extends BaseShardingService<TdIndexHistMapper,TdIndexHist> {
    

    private final IndexInfoService indexInfoService;
    private final TradingDayService tradingDayService;
    private final AkToolsService akToolsService;

    @Autowired
    public TdIndexHistService(IndexInfoService indexInfoService, TradingDayService tradingDayService, AkToolsService akToolsService) {
        this.indexInfoService = indexInfoService;
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }
    
    /**
     * 定时同步任务：每6秒执行一次
     * 非交易时间和非交易日可以执行
     */
    @Scheduled(cron = "*/6 * * * * *")
    @Transactional(rollbackFor = Exception.class)
    public void scheduleSyncIndexHistData() throws Exception {
        syncIndexHistData(true);
    }

    @Transactional(rollbackFor = Exception.class)
    public void syncIndexHistData(boolean isTask) throws Exception {
        if (!isTask || !tradingDayService.isNotTradingTime()) {
            return;
        }
        // 确定同步日期
        int currentDay = Integer.parseInt(LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")));
        int targetDay = (!tradingDayService.isTradingDay(LocalDate.now()) || LocalTime.now().isBefore(LocalTime.of(9, 15)) || LocalTime.now().isAfter(LocalTime.of(22, 30))) ? tradingDayService.getPreviousTradingDay(currentDay): currentDay;
        // 获取所有指数缓存
        List<IndexInfoCacheValue> allIndexes = indexInfoService.getAllIndexCache();
        Optional<IndexInfoCacheValue> infoCacheValue = allIndexes.stream().filter(e->e.getUpdateHistory() == 1 && (e.getIndexHistUpdate() == null || e.getIndexHistUpdate() < targetDay)).findFirst();
        if (infoCacheValue.isEmpty()) {
            return;
        }
        // 同步单个指数的历史数据
        log.info("开始更新指数历史数据 day:{}, indexCode:{}", targetDay, infoCacheValue.get().getIndexCode());
        syncSingleIndexHistData(infoCacheValue.get(), targetDay);
    }


    /**
     * 同步单个指数的历史数据
     *
     * @param indexInfo 指数信息
     * @param targetDay 目标交易日
     */
    @Transactional(rollbackFor = Exception.class)
    public void syncSingleIndexHistData(IndexInfoCacheValue indexInfo, int targetDay) throws Exception {
        String indexCode = indexInfo.getIndexCode();
        String source = indexInfo.getSource();
        log.info("开始同步指数 {} ({}) 的历史行情数据", indexCode, indexInfo.getDisplayName());
        int startDay = indexInfo.getPublishDate() == null ? 0 : indexInfo.getPublishDate();
        String jsonData = akToolsService.stockZhIndexDailyEm(source.toLowerCase() + indexCode, String.valueOf(startDay), String.valueOf(targetDay));
        JSONArray array = JSON.parseArray(jsonData);
        List<TdIndexHist> histData = getIndexHistDataFromJson(array, indexCode);

        Map<Integer, List<TdIndexHist>> map = histData.stream().collect(Collectors.groupingBy(e -> e.getDate() / 10000));
        for (Map.Entry<Integer, List<TdIndexHist>> entry : map.entrySet()) {
            createTable(entry.getKey());
            getBaseMapper().insertOrUpdateBatch(entry.getValue(), entry.getKey());
        }
        indexInfoService.updateHistUpdate(indexInfo.getIndexCode(), targetDay);
        log.info("{} 更新历史数据 {}",indexInfo.getDisplayName(),histData.size());
    }
    
    /**
     * 从JSON数据中解析指数历史数据
     */
    private List<TdIndexHist> getIndexHistDataFromJson(JSONArray array, String indexCode) {
        List<TdIndexHist> result = new ArrayList<>();
        long createTime = System.currentTimeMillis();
        for (int i = 0; i < array.size(); i++) {
            JSONObject item = array.getJSONObject(i);
            TdIndexHist indexHist = new TdIndexHist();
            // 解析日期
            String dateStr = item.getString("date");
            if (StringUtils.isBlank(dateStr)) {
                continue;
            }
            // 转换日期格式为整数 (yyyyMMdd)
            LocalDate date = LocalDate.parse(dateStr);
            int dateInt = Integer.parseInt(date.format(DateTimeFormatter.ofPattern("yyyyMMdd")));
            indexHist.setIndexCode(indexCode);
            indexHist.setDate(dateInt);
            indexHist.setId(indexCode + "_" + dateInt);
            indexHist.setOpenPrice(item.getBigDecimal("open"));
            indexHist.setClosePrice(item.getBigDecimal("close"));
            indexHist.setHighPrice(item.getBigDecimal("high"));
            indexHist.setLowPrice(item.getBigDecimal("low"));
            
            // 成交量可能是整数类型
            Object volumeObj = item.get("volume");
            if (volumeObj instanceof Integer) {
                indexHist.setVolume(new BigDecimal((Integer) volumeObj));
            } else if (volumeObj instanceof Long) {
                indexHist.setVolume(new BigDecimal((Long) volumeObj));
            } else if (volumeObj instanceof BigDecimal) {
                indexHist.setVolume((BigDecimal) volumeObj);
            } else if (volumeObj instanceof Double) {
                indexHist.setVolume(BigDecimal.valueOf((Double) volumeObj));
            }
            
            indexHist.setAmount(item.getBigDecimal("amount"));
            indexHist.setCreateTime(createTime);
            
            result.add(indexHist);
        }
        
        return result;
    }
    


    
    /**
     * 手动触发单个指数同步
     * @param indexCode 指数代码
     */
    @Transactional(rollbackFor = Exception.class)
    public void manualSyncIndexHist(String indexCode) throws Exception {
        IndexInfoCacheValue cacheValue = indexInfoService.getIndexCache(indexCode);
        if(cacheValue == null) {
            throw new ResponseException(20432,"指数未找到");
        }
        syncSingleIndexHistData(cacheValue,tradingDayService.getLstTradingDay());

    }
} 