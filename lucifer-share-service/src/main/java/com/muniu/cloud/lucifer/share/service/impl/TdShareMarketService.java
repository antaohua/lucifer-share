package com.muniu.cloud.lucifer.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.commons.core.annotation.AsyncEventListener;
import com.muniu.cloud.lucifer.commons.core.mybatisplus.BaseShardingService;
import com.muniu.cloud.lucifer.commons.core.utls.SpringContextUtils;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import com.muniu.cloud.lucifer.share.service.model.cache.ShareInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.entity.TdShareMarket;
import com.muniu.cloud.lucifer.share.service.mapper.TdShareMarketMapper;
import com.muniu.cloud.lucifer.share.service.model.dto.SinaStockMarketSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * @author antaohua
 */
@Service
@Slf4j
public class TdShareMarketService extends BaseShardingService<TdShareMarketMapper, TdShareMarket> {

    private static final int SHARDING = 8;

    private static final int TIME_INTERVAL = 3;

    private final ShareInfoService shareInfoService;

    private final TdShareMarketMapper tdShareMarketMapper;




    private final BlockingQueue<TdShareMarket> saveQueue = new LinkedBlockingQueue<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    @Autowired
    public TdShareMarketService(ShareInfoService shareInfoService, TdShareMarketMapper tdShareMarketMapper) {
        this.shareInfoService = shareInfoService;
        this.tdShareMarketMapper = tdShareMarketMapper;
        scheduler.scheduleAtFixedRate(() -> SpringContextUtils.getBean(TdShareMarketService.class).saveData(), 0, TIME_INTERVAL, TimeUnit.SECONDS);
    }

    /**
     * 获取分表表名后缀
     */
    private String getSharding(int day, String shareCode) {
        return day + "_" + (shareCode.hashCode() % SHARDING);
    }


    // 监听事件
    @AsyncEventListener
    public void sinaStockMarketSaveEventHandle(SinaStockMarketSaveEvent event) {
        int day = Integer.parseInt(DateUtil.format(new Date(event.getLoadTime()), DateConstant.DATE_FORMAT_YYYYMMDD));
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
//        List<Integer> previousTradingDays = tradingDayService.getPreviousTradingDays(day, 5);
//        List<TdShareMarket> shareMarkets = null;
//
//        BigDecimal sum = shareMarkets.stream().map(TdShareMarket::getVolume).reduce(BigDecimal.ZERO, BigDecimal::add);
//        BigDecimal average = sum.divide(new BigDecimal(shareMarkets.size()), RoundingMode.HALF_UP);
//        BigDecimal volumeRatio = shareMarketEntity.getVolume().divide(average, 2, RoundingMode.HALF_UP);
//        shareMarketEntity.setVolumeRatio(volumeRatio);
        receiveData(shareMarketEntity);
    }

    @Transactional(rollbackFor = Exception.class)
    public void saveData() {
        List<TdShareMarket> batch = Lists.newArrayList();
        saveQueue.drainTo(batch, 1000);
        if (CollectionUtils.isEmpty(batch)) {
            return;
        }
        Map<String, List<TdShareMarket>> map = batch.stream().collect(Collectors.groupingBy(e -> getSharding(e.getDate(), e.getShareCode())));
        for (Map.Entry<String, List<TdShareMarket>> entry : map.entrySet()) {
            createTable("td_share_market_", entry.getKey());
            int rowCount = tdShareMarketMapper.insertBatch(entry.getValue(), entry.getKey());
            log.info("fetchRemoteStockRealTimeData - rowCount:{}", rowCount);
        }
    }


    // 接收数据
    public void receiveData(TdShareMarket data) {
        try {
            saveQueue.put(data);
            if (saveQueue.size() >= 1000) {
                saveData();
            }
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }
}
