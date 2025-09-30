package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingService;
import com.muniu.cloud.lucifer.share.service.clients.EastmoneyStockHistApiClient;
import com.muniu.cloud.lucifer.share.service.config.ScheduledInterface;
import com.muniu.cloud.lucifer.share.service.model.cache.ShareInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import com.muniu.cloud.lucifer.share.service.entity.TdShareHist;
import com.muniu.cloud.lucifer.share.service.mapper.TdShareHistMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class TdShareHistService extends BaseShardingService<TdShareHistMapper, TdShareHist> implements ScheduledInterface {


    private final ShareInfoService shareInfoService;

    private final TradingDateTimeService tradingDayService;

    private final EastmoneyStockHistApiClient akToolsService;


    @Autowired
    public TdShareHistService(ShareInfoService shareInfoService,
                              TradingDateTimeService tradingDayService, EastmoneyStockHistApiClient akToolsService) {
        this.shareInfoService = shareInfoService;
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }






    @Transactional(rollbackFor = Exception.class)
    public void scheduled() throws Exception {
        LocalTime now = LocalTime.now();
        int day = Integer.parseInt(LocalDate.now().format(DATE_FORMATTER_YYYYMMDD));
        if (!tradingDayService.isTradingDay(LocalDate.now()) || now.isBefore(LocalTime.of(9, 0))) {
            day = tradingDayService.getPreviousTradingDay(day);
        }

        List<ShareInfoCacheValue> shareInfoCacheValues = shareInfoService.getAll();
        ShareInfoCacheValue cacheValue = null;
        for (ShareInfoCacheValue entry : shareInfoCacheValues) {
            if (entry.getStatus() != ShareStatus.DEMISTED && entry.getHistoryUpdateDate() < day && entry.getListDate() < day) {
                cacheValue = entry;
                break;
            }
        }
        if (cacheValue == null) {
            return;
        }
        Integer fastDay = Math.max(cacheValue.getHistoryUpdateDate(), 0);
        if(fastDay == 0){
            int year = day / 10000;
            TdShareHist hist;
            int listYear = cacheValue.getListDate() > 0 ? cacheValue.getListDate() / 10000 : 1990;
            do {
                createTable("td_share_hist_", String.valueOf(year));
                hist = getBaseMapper().selectShareLastDate(cacheValue.getCode(), year);
                if (hist != null) {
                    fastDay = hist.getDate();
                    break;
                }
                year--;
            }while (year >= listYear);
        }

        if (fastDay == day) {
            if (fastDay != cacheValue.getHistoryUpdateDate()) {
                shareInfoService.updateShareHistoryUpdateDate(cacheValue.getCode(), day);
            }
            log.info("已更新无需再更新 day:{},fastDay:{},shareCode={}", day, fastDay, cacheValue.getCode());
            return;
        }
        fastDay = fastDay == 0 ? tradingDayService.getNextTradingDay(cacheValue.getListDate()) : tradingDayService.getNextTradingDay(fastDay);
        log.info("开始更新股票历史数据 fastDay:{},day:{},shareCode:{}", fastDay, day, cacheValue.getCode());
        String jsonString = akToolsService.stockZhAHist(cacheValue.getCode(), String.valueOf(fastDay), String.valueOf(day));
        if(StringUtils.isBlank(jsonString)){
            log.info("股票历史数据为空 fastDay:{},day:{},shareCode:{}", fastDay, day, cacheValue.getCode());
            return;
        }
        JSONArray jsonArray = JSON.parseArray(jsonString);
        List<TdShareHist> list = jsonArray.stream().filter(Objects::nonNull).map(e -> getShareHistEntity((JSONObject) e, System.currentTimeMillis())).toList();

        Map<Integer, List<TdShareHist>> map = list.stream().collect(Collectors.groupingBy(e -> e.getDate() / 10000));
        map.forEach((k, v) -> createTable("td_share_hist_", String.valueOf(k)));
        map.forEach((k, v) -> getBaseMapper().insertOrUpdateBatch(v, k));
        shareInfoService.updateShareHistoryUpdateDate(cacheValue.getCode(), day);
        log.info("更新股票历史数据完成 fastDay:{},day:{},shareCode:{},listSize:{}", fastDay, day, cacheValue.getCode(), list.size());
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




}
