package com.muniu.cloud.lucifer.share.service.impl;

import cn.hutool.core.collection.ConcurrentHashSet;
import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import com.muniu.cloud.lucifer.share.service.model.cache.ShareInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.entity.TdShareMarket;
import com.muniu.cloud.lucifer.share.service.mapper.TdShareMarketMapper;
import com.muniu.cloud.lucifer.share.service.model.dto.SinaStockMarketSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TdShareMarketService extends BaseShardingService<TdShareMarketMapper, TdShareMarket> {

    private static final int SHARDING = 8;

    private final ApplicationEventPublisher publisher;

    private final ShareInfoService shareInfoService;

    private final TdShareMarketMapper tdShareMarketMapper;

    private static final ConcurrentHashSet<String> TABLES = new ConcurrentHashSet<>();

    private final TradingDayService tradingDayService;

    private final AkToolsService akToolsService;

    @Autowired
    public TdShareMarketService(ShareInfoService shareInfoService, ApplicationEventPublisher publisher, TdShareMarketMapper tdShareMarketMapper, TradingDayService tradingDayService, AkToolsService akToolsService) {
        this.publisher = publisher;
        this.shareInfoService = shareInfoService;
        this.tdShareMarketMapper = tdShareMarketMapper;
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }

    /**
     * 获取分表表名后缀
     */
    private String getSharding(int day,String shareCode) {
        return day + "_" + (shareCode.hashCode() % SHARDING);
    }

    @Scheduled(cron = "0 * * * * MON-FRI")
    @Transactional(rollbackFor = Exception.class)
    public void fetchRemoteStockRealTimeData() throws Exception {
        if(!tradingDayService.isTradingTime(true)){
            return;
        }
        long startTime = System.currentTimeMillis();
        int day =  Integer.parseInt(DateUtil.format(new Date(startTime), DateConstant.DATE_FORMAT_YYYYMMDD));
        String jsonData = akToolsService.stockZhASpotEm();
        if(StringUtils.isBlank(jsonData)){
            return;
        }
        JSONArray array = JSON.parseArray(jsonData);
        List<TdShareMarket> data = array.stream().map(e -> jsonToTdShareMarket((JSONObject) e,day,startTime)).filter(e -> shareInfoService.getShareInfoCache(e.getShareCode()) != null).toList();
        Map<String,List<TdShareMarket>> map = data.stream().collect(Collectors.groupingBy(e ->getSharding(e.getDate(),e.getShareCode())));
        data.forEach(publisher::publishEvent);
        for (Map.Entry<String,List<TdShareMarket>> entry : map.entrySet()) {
            createTable(entry.getKey());
            int rowCount = tdShareMarketMapper.insertBatch(entry.getValue(),entry.getKey());
            log.info("fetchRemoteStockRealTimeData - rowCount:{}", rowCount);
        }
    }




    // 监听事件
    @EventListener
    public void sinaStockMarketSaveEventHandle(SinaStockMarketSaveEvent event) {
        int day =  Integer.parseInt(DateUtil.format(new Date(event.getLoadTime()), DateConstant.DATE_FORMAT_YYYYMMDD));
        TdShareMarket shareMarketEntity = new TdShareMarket();
        shareMarketEntity.setDate(day);
        shareMarketEntity.setCreateTime(event.getLoadTime());
        shareMarketEntity.setTime(Integer.parseInt(StringUtils.replaceAll(event.getTicktime(), ":", "")));
        shareMarketEntity.setShareCode(event.getCode());
        shareMarketEntity.setId(shareMarketEntity.getShareCode() + "-" + shareMarketEntity.getTime());
        shareMarketEntity.setLatestPrice(new BigDecimal(event.getTrade()));
        shareMarketEntity.setChangeRate(new BigDecimal(event.getChangepercent()));
        shareMarketEntity.setChangeAmount(new BigDecimal(event.getPricechange()));
        shareMarketEntity.setVolume(new BigDecimal(event.getVolume()));
        shareMarketEntity.setTurnover(new BigDecimal(event.getAmount()));
        shareMarketEntity.setAmplitude(event.getAmplitude());
        shareMarketEntity.setHighest(new BigDecimal(event.getHigh()));
        shareMarketEntity.setLowest(new BigDecimal(event.getLow()));
        shareMarketEntity.setOpeningPrice(new BigDecimal(event.getOpen()));
        shareMarketEntity.setPreviousClose(new BigDecimal(event.getSettlement()));
        shareMarketEntity.setVolumeRatio(jsonObject.getBigDecimal("量比"));
        shareMarketEntity.setTurnoverRate(new BigDecimal(event.getTurnoverratio()));
        shareMarketEntity.setDynamicPe(new BigDecimal(event.getPer()));
        shareMarketEntity.setPbRatio(new BigDecimal(event.getPb()));
        shareMarketEntity.setTotalMarketCap(new BigDecimal(event.getMktcap()));
        shareMarketEntity.setCirculatingMarketCap(new BigDecimal(event.getNmc()));

        //没有昨日收盘价时和今日开盘价 不做涨停和跌停计算
        if (shareMarketEntity.getPreviousClose() != null && shareMarketEntity.getOpeningPrice() != null) {
            ShareInfoCacheValue cacheValue = shareInfoService.getShareInfoCache(shareMarketEntity.getShareCode());
            BigDecimal limitDown = cacheValue == null ? BigDecimal.ZERO : cacheValue.getSection().minPrice(shareMarketEntity.getPreviousClose(), cacheValue.getStatus());
            BigDecimal limitUp = cacheValue == null ? BigDecimal.ZERO : cacheValue.getSection().maxPrice(shareMarketEntity.getPreviousClose(), cacheValue.getStatus());
            shareMarketEntity.setLimitUp(limitUp);
            shareMarketEntity.setLimitDown(limitDown);
        }
    }


    /**
     * 获取在线股票实时数据
     */
    @Transactional(rollbackFor = Exception.class)
    public List<TdShareMarket> getOnlineShareMarket() throws Exception {
        if (!tradingDayService.isTradingDay(LocalDate.now())) {
            return null;
        }
        long startTime = System.currentTimeMillis();
        int day = Integer.parseInt(DateUtil.format(new Date(startTime), DateConstant.DATE_FORMAT_YYYYMMDD));
        String jsonData = akToolsService.stockZhASpotEm();
        if (StringUtils.isBlank(jsonData)) {
            return null;
        }
        JSONArray array = JSON.parseArray(jsonData);
        return array.stream().map(e -> jsonToTdShareMarket((JSONObject) e,day,startTime)).filter(e -> shareInfoService.getShareInfoCache(e.getShareCode()) != null).toList();
    }




    private TdShareMarket jsonToTdShareMarket(JSONObject jsonObject,int date,long time) {
        TdShareMarket shareMarketEntity = new TdShareMarket();
        shareMarketEntity.setDate(date);
        shareMarketEntity.setCreateTime(time);
        shareMarketEntity.setTime(Integer.parseInt(DateUtil.format(new Date(time), "HHmmssSSS")));
        shareMarketEntity.setShareCode(jsonObject.getString("代码"));
        shareMarketEntity.setId(shareMarketEntity.getShareCode() + "-" + shareMarketEntity.getTime());
        shareMarketEntity.setLatestPrice(jsonObject.getBigDecimal("最新价"));
        shareMarketEntity.setChangeRate(jsonObject.getBigDecimal("涨跌幅"));
        shareMarketEntity.setChangeAmount(jsonObject.getBigDecimal("涨跌额"));
        shareMarketEntity.setVolume(jsonObject.getBigDecimal("成交量"));
        shareMarketEntity.setTurnover(jsonObject.getBigDecimal("成交额"));
        shareMarketEntity.setAmplitude(jsonObject.getBigDecimal("振幅"));
        shareMarketEntity.setHighest(jsonObject.getBigDecimal("最高"));
        shareMarketEntity.setLowest(jsonObject.getBigDecimal("最低"));
        shareMarketEntity.setOpeningPrice(jsonObject.getBigDecimal("今开"));
        shareMarketEntity.setPreviousClose(jsonObject.getBigDecimal("昨收"));
        shareMarketEntity.setVolumeRatio(jsonObject.getBigDecimal("量比"));
        shareMarketEntity.setTurnoverRate(jsonObject.getBigDecimal("换手率"));
        shareMarketEntity.setDynamicPe(jsonObject.getBigDecimal("市盈率-动态"));
        shareMarketEntity.setPbRatio(jsonObject.getBigDecimal("市净率"));
        shareMarketEntity.setTotalMarketCap(jsonObject.getBigDecimal("总市值"));
        shareMarketEntity.setCirculatingMarketCap(jsonObject.getBigDecimal("流通市值"));
        shareMarketEntity.setSpeed(jsonObject.getBigDecimal("涨速"));
        shareMarketEntity.setFiveMinuteChange(jsonObject.getBigDecimal("5分钟涨跌"));
        //没有昨日收盘价时和今日开盘价 不做涨停和跌停计算
        if (shareMarketEntity.getPreviousClose() != null && shareMarketEntity.getOpeningPrice() != null) {
            ShareInfoCacheValue cacheValue = shareInfoService.getShareInfoCache(shareMarketEntity.getShareCode());
            BigDecimal limitDown = cacheValue == null ? BigDecimal.ZERO : cacheValue.getSection().minPrice(shareMarketEntity.getPreviousClose(), cacheValue.getStatus());
            BigDecimal limitUp = cacheValue == null ? BigDecimal.ZERO : cacheValue.getSection().maxPrice(shareMarketEntity.getPreviousClose(), cacheValue.getStatus());
            shareMarketEntity.setLimitUp(limitUp);
            shareMarketEntity.setLimitDown(limitDown);
        }
        return shareMarketEntity;
    }













}
