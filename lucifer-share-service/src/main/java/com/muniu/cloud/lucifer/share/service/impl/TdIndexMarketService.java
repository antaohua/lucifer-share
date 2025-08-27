package com.muniu.cloud.lucifer.share.service.impl;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import com.muniu.cloud.lucifer.share.service.constant.ShareIndexType;
import com.muniu.cloud.lucifer.share.service.entity.TdIndexMarket;
import com.muniu.cloud.lucifer.share.service.mapper.TdIndexMarketMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 指数行情服务
 */
@Service
@Slf4j
public class TdIndexMarketService extends BaseShardingService<TdIndexMarketMapper, TdIndexMarket> {

    
    private final TradingDayService tradingDayService;

    private final AkToolsService akToolsService;

    private final AtomicInteger indexTypeIndex = new AtomicInteger(0);

    @Autowired
    public TdIndexMarketService(TradingDayService tradingDayService, AkToolsService akToolsService) {
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }
    
    /**
     * 从API获取指数行情数据并入库
     * 每隔1分钟执行一次
     */
    @Scheduled(initialDelay = 10 * 1000, fixedRate = 20 * 1000)
    @Transactional(rollbackFor = Exception.class)
    public void syncIndexMarketData() throws Exception {
        // 判断是否是交易日
        if (!tradingDayService.isTradingTime(true)) {
            return;
        }
        log.info("开始同步指数行情数据");
        // 获取所有类型的指数数据
        if(indexTypeIndex.get() > ShareIndexType.values().length - 1){
            indexTypeIndex.set(0);
        }
        ShareIndexType indexType = ShareIndexType.values()[indexTypeIndex.getAndIncrement()];
        long startTime = System.currentTimeMillis();
        int day = Integer.parseInt(DateUtil.format(new Date(startTime), DateConstant.DATE_FORMAT_YYYYMMDD));
        String jsonData = akToolsService.stockZhIndexSpotEm(indexType.getName());
        if (StringUtils.isBlank(jsonData)) {
            return;
        }
        JSONArray array = JSON.parseArray(jsonData);
        List<TdIndexMarket> typeData = array.stream()
                .map(item -> jsonToTdIndexMarket((JSONObject) item, day, startTime))
                .toList();
        if (CollectionUtils.isEmpty(typeData)) {
            return;
        }
        createTable(day/100);
        // 批量插入数据
        getBaseMapper().insertBatch(typeData, String.valueOf(day/100));
        log.info("同步指数行情数据成功，共{}条", typeData.size());
    }

    
    /**
     * 将JSON对象转换为TdIndexMarket实体
     * @param jsonObject JSON对象
     * @param date 日期
     * @param time 时间戳
     * @return TdIndexMarket实体
     */
    private TdIndexMarket jsonToTdIndexMarket(JSONObject jsonObject, int date, long time) {
        TdIndexMarket indexMarketEntity = new TdIndexMarket();
        indexMarketEntity.setDate(date);
        indexMarketEntity.setCreateTime(time);
        indexMarketEntity.setTime(Integer.parseInt(DateUtil.format(new Date(time), "HHmmssSSS")));
        indexMarketEntity.setIndexCode(jsonObject.getString("代码"));
        indexMarketEntity.setIndexName(jsonObject.getString("名称"));
        indexMarketEntity.setId(indexMarketEntity.getIndexCode() + "-" + date + "-" + indexMarketEntity.getTime());
        
        // 设置指标数据
        indexMarketEntity.setLatestPrice(jsonObject.getBigDecimal("最新价"));
        indexMarketEntity.setChangeAmount(jsonObject.getBigDecimal("涨跌额"));
        indexMarketEntity.setChangeRate(jsonObject.getBigDecimal("涨跌幅"));
        indexMarketEntity.setVolume(jsonObject.getBigDecimal("成交量"));
        indexMarketEntity.setTurnover(jsonObject.getBigDecimal("成交额"));
        indexMarketEntity.setAmplitude(jsonObject.getBigDecimal("振幅"));
        indexMarketEntity.setHighest(jsonObject.getBigDecimal("最高"));
        indexMarketEntity.setLowest(jsonObject.getBigDecimal("最低"));
        indexMarketEntity.setOpeningPrice(jsonObject.getBigDecimal("今开"));
        indexMarketEntity.setPreviousClose(jsonObject.getBigDecimal("昨收"));
        // 量比可能为"-"，需要处理
        String volumeRatioStr = jsonObject.getString("量比");
        if (StringUtils.isNotBlank(volumeRatioStr) && !"-".equals(volumeRatioStr)) {
            indexMarketEntity.setVolumeRatio(new BigDecimal(volumeRatioStr));
        }
        return indexMarketEntity;
    }
    

    

} 