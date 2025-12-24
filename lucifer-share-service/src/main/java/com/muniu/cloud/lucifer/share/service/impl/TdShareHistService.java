package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingService;
import com.muniu.cloud.lucifer.share.service.clients.EastmoneyShareHistApiClient;
import com.muniu.cloud.lucifer.share.service.config.ScheduledInterface;
import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
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
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TdShareHistService extends BaseShardingService<TdShareHistMapper, TdShareHist> implements ScheduledInterface {


    private final ShareInfoService shareInfoService;

    private final TradingDateTimeService tradingDayService;

    private final EastmoneyShareHistApiClient akToolsService;


    @Autowired
    public TdShareHistService(ShareInfoService shareInfoService,
                              TradingDateTimeService tradingDayService, EastmoneyShareHistApiClient akToolsService) {
        this.shareInfoService = shareInfoService;
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }






    @Transactional(rollbackFor = Exception.class)
    public void scheduled() throws Exception {
        int day = tradingDayService.isTradingTime() ? tradingDayService.getPreviousTradingDay(tradingDayService.getLstTradingDay()) : tradingDayService.getLstTradingDay();
        List<ShareInfoCacheValue> shareInfoCacheValues = shareInfoService.getAll();
        ShareInfoCacheValue cacheValue = shareInfoCacheValues.stream().filter(e-> e.getStatus() != ShareStatus.DEMISTED && e.getHistoryUpdateDate() < day && e.getListDate() < day).findFirst().orElse(null);
        if (cacheValue == null) {
            return;
        }
        int fastDay = cacheValue.getHistoryUpdateDate();
        int fastYear = fastDay == 0 ? 1990: fastDay / 10000;
        int lastYear = day / 10000;
        for (int i = fastYear; i <= lastYear; i++) {
            createTable("td_share_hist_", String.valueOf(i));
        }

        if (fastDay == day && fastDay != cacheValue.getHistoryUpdateDate()) {
            shareInfoService.updateShareHistoryUpdateDate(cacheValue.getCode(), day);
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

        BigDecimal yesterdayClosePrice = new BigDecimal(0).add(shareHistEntity.getClosePrice()).subtract(shareHistEntity.getChangeAmount());
        shareHistEntity.setPreviousClose(yesterdayClosePrice);
        ShareBoard section = ShareBoard.fromKey(shareHistEntity.getShareCode());
        ShareStatus status = ShareStatus.getStatus(item.getString("name"));
        BigDecimal limitDown = section.minPrice(yesterdayClosePrice, status);
        BigDecimal limitUp = section.maxPrice(yesterdayClosePrice, status);
        shareHistEntity.setLimitUp(limitUp);
        shareHistEntity.setLimitDown(limitDown);
        return shareHistEntity;
    }




}
