package com.muniu.cloud.lucifer.share.service.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.share.service.model.cache.ShareInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.constant.AdjustConstant;
import com.muniu.cloud.lucifer.share.service.constant.PeriodConstant;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import com.muniu.cloud.lucifer.share.service.entity.TdShareHist;
import com.muniu.cloud.lucifer.share.service.entity.TdShareMarket;
import com.muniu.cloud.lucifer.share.service.mapper.TdShareHistMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.muniu.cloud.lucifer.commons.utils.constants.DateConstant.DATE_FORMATTER_YYYYMMDD;

@Service
@Slf4j
public class TdShareHistService {


    private final ShareInfoService shareInfoService;

    private final TdShareMarketService tdShareMarketService;

    private final TdShareHistMapper tdShareHistMapper;

    private final TradingDayService tradingDayService;

    private final AkToolsService akToolsService;

    private final Object lock = new Object();

    private final ConcurrentHashSet<Integer> tables = new ConcurrentHashSet<>();

    @Autowired
    public TdShareHistService(ShareInfoService shareInfoService,
                              TdShareMarketService tdShareMarketService, TdShareHistMapper tdShareHistMapper,
                              TradingDayService tradingDayService, AkToolsService akToolsService) {
        this.shareInfoService = shareInfoService;
        this.tdShareMarketService = tdShareMarketService;
        this.tdShareHistMapper = tdShareHistMapper;
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }


    /**
     * 根据实数数据更新股票历史数据
     */
    @Scheduled(cron = "0 0 16 * * ?")
    @Transactional(rollbackFor = Exception.class)
    public void updateShareHistByRealTimeData() throws Exception {

        if (!tradingDayService.isTradingDay(LocalDate.now())) {
            return;
        }
        List<TdShareMarket> marketEntities = tdShareMarketService.getOnlineShareMarket();
        if (marketEntities == null) {
            return;
        }
        int day = Integer.parseInt(LocalDate.now().format(DATE_FORMATTER_YYYYMMDD));
        Map<String, ShareInfoCacheValue> shareInfoCacheValueMap = shareInfoService.getAll();

        List<String> codes = tdShareHistMapper.selectLastDateShareCodeExcludingToday(day, tradingDayService.getPreviousTradingDay(day));

        List<TdShareHist> list = marketEntities.stream().filter(e -> shareInfoCacheValueMap.get(e.getShareCode()) != null)
                .filter(e -> shareInfoCacheValueMap.get(e.getShareCode()).getListDate() == day || codes.contains(e.getShareCode()))
                .filter(e -> shareInfoCacheValueMap.get(e.getShareCode()).getHistoryUpdateDate() == tradingDayService.getPreviousTradingDay(day))
                .map(e -> new TdShareHist(e, System.currentTimeMillis())).toList();
        if (list.isEmpty()) {
            return;
        }
        createTable(day / 10000);
        int count = tdShareHistMapper.insertOrUpdateBatch(list, day / 10000);
        log.info("更新股票历史数据完成 day:{},listSize:{}", day, count);
        shareInfoService.updateShareHistoryUpdateDate(list.stream().map(TdShareHist::getShareCode).toList(), day);
    }


    @Scheduled(cron = "*/6 * * * * *")
    @Transactional(rollbackFor = Exception.class)
    public void fetchRemoteStockHistData() throws IOException {
        LocalTime now = LocalTime.now();
        if (tradingDayService.isTradingDay(LocalDate.now()) && (now.isAfter(LocalTime.of(8, 15)) && now.isBefore(LocalTime.of(18, 0)))) {
            return;
        }
        int day = Integer.parseInt(LocalDate.now().format(DATE_FORMATTER_YYYYMMDD));
        if (!tradingDayService.isTradingDay(LocalDate.now()) || now.isBefore(LocalTime.of(9, 0))) {
            day = tradingDayService.getPreviousTradingDay(day);
        }

        Map<String, ShareInfoCacheValue> shareInfoCacheValueMap = shareInfoService.getAll();
        Map.Entry<String, ShareInfoCacheValue> cacheValueEntry = null;
        for (Map.Entry<String, ShareInfoCacheValue> entry : shareInfoCacheValueMap.entrySet()) {
            if (entry.getValue().getStatus() != ShareStatus.DEMISTED && entry.getValue().getHistoryUpdateDate() < day && entry.getValue().getListDate() < day) {
                cacheValueEntry = entry;
                break;
            }
        }
        if (cacheValueEntry == null) {
            return;
        }
        Integer fastDay = Math.max(cacheValueEntry.getValue().getHistoryUpdateDate(), 0);
        if(fastDay == 0){
            int year = day / 10000;
            TdShareHist hist;
            int listYear = cacheValueEntry.getValue().getListDate() > 0 ? cacheValueEntry.getValue().getListDate() / 10000 : 1990;
            do {
                createTable(year);
                hist = tdShareHistMapper.selectShareLastDate(cacheValueEntry.getKey(), year);
                if (hist != null) {
                    fastDay = hist.getDate();
                    break;
                }
                year--;
            }while (year >= listYear);
        }

        if (fastDay == day) {
            if (fastDay != cacheValueEntry.getValue().getHistoryUpdateDate()) {
                shareInfoService.updateShareHistoryUpdateDate(cacheValueEntry.getKey(), day);
            }
            log.info("已更新无需再更新 day:{},fastDay:{},shareCode={}", day, fastDay, cacheValueEntry.getKey());
            return;
        }
        fastDay = fastDay == 0 ? tradingDayService.getNextTradingDay(cacheValueEntry.getValue().getListDate()) : tradingDayService.getNextTradingDay(fastDay);
        log.info("开始更新股票历史数据 fastDay:{},day:{},shareCode:{}", fastDay, day, cacheValueEntry.getKey());
        String jsonString = akToolsService.stockZhAHist(cacheValueEntry.getKey(), PeriodConstant.DAY, AdjustConstant.NONE, String.valueOf(fastDay), String.valueOf(day));
        if(StringUtils.isBlank(jsonString)){
            log.info("股票历史数据为空 fastDay:{},day:{},shareCode:{}", fastDay, day, cacheValueEntry.getKey());
            return;
        }
        JSONArray jsonArray = JSON.parseArray(jsonString);
        List<TdShareHist> list = jsonArray.stream().filter(Objects::nonNull).map(e -> getShareHistEntity((JSONObject) e, System.currentTimeMillis())).toList();

        Map<Integer, List<TdShareHist>> map = list.stream().collect(Collectors.groupingBy(e -> e.getDate() / 10000));
        map.forEach((k, v) -> createTable(k));
        map.forEach((k, v) -> tdShareHistMapper.insertOrUpdateBatch(v, k));
        shareInfoService.updateShareHistoryUpdateDate(cacheValueEntry.getKey(), day);
        log.info("更新股票历史数据完成 fastDay:{},day:{},shareCode:{},listSize:{}", fastDay, day, cacheValueEntry.getKey(), list.size());
    }

    private TdShareHist getShareHistEntity(JSONObject item, long createTime) {
        TdShareHist shareHistEntity = new TdShareHist();
        shareHistEntity.setShareCode(item.getString("股票代码"));
        shareHistEntity.setDate(Integer.parseInt(item.getString("日期").substring(0, 10).replace("-", "")));
        shareHistEntity.setId(shareHistEntity.getShareCode() + "_" + shareHistEntity.getDate());
        shareHistEntity.setOpenPrice(BigDecimal.valueOf(item.getDouble("开盘")));
        shareHistEntity.setClosePrice(BigDecimal.valueOf(item.getDouble("收盘")));
        shareHistEntity.setHighPrice(BigDecimal.valueOf(item.getDouble("最高")));
        shareHistEntity.setLowPrice(BigDecimal.valueOf(item.getDouble("最低")));
        shareHistEntity.setVolume(BigDecimal.valueOf(item.getLong("成交量")));
        shareHistEntity.setAmount(BigDecimal.valueOf(item.getDouble("成交额")));
        shareHistEntity.setAmplitude(BigDecimal.valueOf(item.getDouble("振幅")));
        shareHistEntity.setChangeRate(BigDecimal.valueOf(item.getDouble("涨跌幅")));
        shareHistEntity.setChangeAmount(BigDecimal.valueOf(item.getDouble("涨跌额")));
        shareHistEntity.setTurnoverRate(BigDecimal.valueOf(item.getDouble("换手率")));
        shareHistEntity.setCreateTime(createTime);
        ShareInfoCacheValue cacheValue = shareInfoService.getShareInfoCache(shareHistEntity.getShareCode());
        BigDecimal yesterdayClosePrice = new BigDecimal(0).add(shareHistEntity.getClosePrice()).subtract(shareHistEntity.getChangeAmount());
        shareHistEntity.setPreviousClose(yesterdayClosePrice);
        BigDecimal limitDown = cacheValue.getSection().minPrice(yesterdayClosePrice, cacheValue.getStatus());
        BigDecimal limitUp = cacheValue.getSection().maxPrice(yesterdayClosePrice, cacheValue.getStatus());
        shareHistEntity.setLimitUp(limitUp);
        shareHistEntity.setLimitDown(limitDown);
        return shareHistEntity;
    }


    @Transactional(rollbackFor = Exception.class)
    public void createTable(int year) {
        if (tables.contains(year)) {
            return;
        }
        synchronized (lock) {
            if (tables.isEmpty()) {
                tables.addAll(tdShareHistMapper.selectTable().stream().map(Integer::valueOf).toList());
            }
            if (tables.contains(year)) {
                return;
            }
            tdShareHistMapper.createTable(String.valueOf(year));
            tables.add(year);
        }
    }

}
