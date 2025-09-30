package com.muniu.cloud.lucifer.share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muniu.cloud.lucifer.commons.core.config.AsyncEventListener;
import com.muniu.cloud.lucifer.commons.core.utls.SpringContextUtils;
import com.muniu.cloud.lucifer.commons.model.page.PageParams;
import com.muniu.cloud.lucifer.commons.model.page.PageResult;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import com.muniu.cloud.lucifer.share.service.constant.LuciferShareConstant;
import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
import com.muniu.cloud.lucifer.share.service.constant.ShareExchange;
import com.muniu.cloud.lucifer.share.service.dao.ShareInfoDao;
import com.muniu.cloud.lucifer.share.service.model.cache.ShareInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;
import com.muniu.cloud.lucifer.share.service.mapper.ShareInfoMapper;
import com.muniu.cloud.lucifer.share.service.model.SimpleShareInfo;
import com.muniu.cloud.lucifer.share.service.model.dto.SinaStockMarketSaveEvent;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ShareInfoService extends ServiceImpl<ShareInfoMapper,ShareInfo> {


    private final RedisTemplate<String,String> redisTemplate;

    private static final ConcurrentHashMap<String, ShareInfoCacheValue> SHARE_INFO_CACHE = new ConcurrentHashMap<>();

    private final TradingDateTimeService tradingDayService;


    private final ShareInfoDao shareInfoDao ;


    @Autowired
    public ShareInfoService(RedisTemplate<String,String> redisTemplate ,TradingDateTimeService tradingDayService, ShareInfoDao shareInfoDao) {
        this.tradingDayService = tradingDayService;
        this.redisTemplate = redisTemplate;
        this.shareInfoDao = shareInfoDao;
    }

    private static final String REDIS_SHARE_STATUS = "SHARE:DATE:STATUS:";
    private static final String REDIS_SHARE_SET = "SHARE:DATE:SET:";
    @AsyncEventListener
    public void sinaStockMarketSaveEventHandle(SinaStockMarketSaveEvent event) {
        String dayString = LuciferShareConstant.LAST_TRADING_DATA.format(DateConstant.DATE_FORMATTER_YYYYMMDD);
        String statusKey = REDIS_SHARE_STATUS + event.getCode() + ":" + dayString;
        String setKey = REDIS_SHARE_SET + dayString;
        if (!redisTemplate.hasKey(statusKey)) {
            redisTemplate.opsForHash().putAll(statusKey,getShareStatusMap( event));
            redisTemplate.expire(statusKey, Duration.ofDays(10L));
        }
        boolean isNew = redisTemplate.hasKey(setKey);
        redisTemplate.opsForSet().add(setKey, event.getCode());
        if (isNew) {
            redisTemplate.expire(setKey, Duration.ofDays(10L));
        }
    }



    public Set<String> getShareCodes() {
        String dayString = LuciferShareConstant.LAST_TRADING_DATA.format(DateConstant.DATE_FORMATTER_YYYYMMDD);
        String setKey = REDIS_SHARE_SET + dayString;
        if (!redisTemplate.hasKey(setKey)) {
            return Sets.newHashSet();
        }
        return redisTemplate.opsForSet().members(setKey);
    }

    public Map<String, String> getShareInfoCache(String shareCode) {
        String dayString = LuciferShareConstant.LAST_TRADING_DATA.format(DateConstant.DATE_FORMATTER_YYYYMMDD);
        String statusKey = REDIS_SHARE_STATUS + shareCode + ":" + dayString;
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        return hashOps.entries(statusKey);
    }



    @Transactional(rollbackFor = Exception.class)
    public void updateShareHistoryUpdateDate(String shareCode, int day) {
        getAll();
        ShareInfoCacheValue shareInfoCacheValue = SHARE_INFO_CACHE.get(shareCode);
        if (shareInfoCacheValue != null) {
            getBaseMapper().updateShareHistoryUpdateDate(shareCode, day);
            shareInfoCacheValue.setHistoryUpdateDate(day);
        }
    }





    public synchronized Map<String, ShareInfoCacheValue> getAll() {
        String dayString = LuciferShareConstant.LAST_TRADING_DATA.format(DateConstant.DATE_FORMATTER_YYYYMMDD);
        return Maps.newHashMap(SHARE_INFO_CACHE);
    }




    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 01 * * ?")
    public void shanghaiShareList() throws IOException {
        getAll();
    }


    private Map<String,String> getShareStatusMap(SinaStockMarketSaveEvent stockMarket) {
        Map<String,String> shareInfo = Maps.newHashMap();
        shareInfo.put("shareCode", stockMarket.getCode());
        shareInfo.put("shareName", stockMarket.getName());
        shareInfo.put("shareExchange", ShareExchange.getExchange(stockMarket.getCode()).getCode());
        shareInfo.put("shareBoard", ShareBoard.getBoard(stockMarket.getCode()).getKey());
        shareInfo.put("shareStatus",ShareStatus.getStatus(stockMarket.getName()).getCode());
        return shareInfo;
    }


    public PageResult<ShareInfo> getPageList(PageParams pageParams) {
        QueryWrapper<ShareInfo> query = new QueryWrapper<>();
        if (MapUtils.isNotEmpty(pageParams.getPageParams())) {
            for (Map.Entry<String, String> entry : pageParams.getPageParams().entrySet()) {
                if (StringUtils.equals(entry.getKey(), "shareCode") && StringUtils.isNotBlank(entry.getValue())) {
                    query.like("id", "%" + entry.getValue().trim() + "%");
                }
                if (StringUtils.equals(entry.getKey(), "shareName") && StringUtils.isNotBlank(entry.getValue())) {
                    query.like("share_name", "%" + entry.getValue().trim() + "%");
                }
                if (StringUtils.equals(entry.getKey(), "shareStatus") && StringUtils.isNotBlank(entry.getValue())) {
                    String[] statusArray = StringUtils.split(entry.getValue(), ",");
                    query.in("share_status", Arrays.asList(statusArray));
                }
            }
        }
        Long total = getBaseMapper().selectCount(query);
        Page<ShareInfo> page = new Page<>(pageParams.getPageNum(), pageParams.getPageSize(),total);
        Page<ShareInfo> resultPage = getBaseMapper().selectPage(page, query);
        log.info("page-size:{}", resultPage.getSize());
        return new PageResult<>(pageParams.getPageNum(), pageParams.getPageSize(), resultPage.getTotal(), resultPage.getRecords());
    }



}
