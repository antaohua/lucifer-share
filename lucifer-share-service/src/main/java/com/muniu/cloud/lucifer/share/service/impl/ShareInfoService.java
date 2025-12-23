package com.muniu.cloud.lucifer.share.service.impl;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muniu.cloud.lucifer.commons.core.annotation.AsyncEventListener;
import com.muniu.cloud.lucifer.share.service.constant.LuciferShareConstant;
import com.muniu.cloud.lucifer.share.service.constant.ShareBoard;
import com.muniu.cloud.lucifer.share.service.constant.ShareExchange;
import com.muniu.cloud.lucifer.share.service.model.cache.ShareInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import com.muniu.cloud.lucifer.share.service.model.dto.SinaStockMarketSaveEvent;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@Slf4j
@Transactional(rollbackFor = Exception.class)
public class ShareInfoService{


    private final RedisTemplate<String, String> redisTemplate;

    private final RedissonClient redisson;

    @Autowired
    public ShareInfoService(RedisTemplate<String, String> redisTemplate, RedissonClient redisson) {
        this.redisson = redisson;
        this.redisTemplate = redisTemplate;
    }

    @PostConstruct
    public void init(){
        RTopic topic = redisson.getTopic(LuciferShareConstant.REDIS_STOCK_MARKET);
        topic.addListener(SinaStockMarketSaveEvent.class, (channel, msg) -> {
            sinaStockMarketSaveEventHandle(msg);
        });
    }


    public void sinaStockMarketSaveEventHandle(SinaStockMarketSaveEvent event) {
        String redisShareSetKey = LuciferShareConstant.getRedisShareSetKey();
        String redisShareStatusKey = LuciferShareConstant.getRedisShareStatusKey(event.getCode());
        if (!redisTemplate.hasKey(redisShareStatusKey)) {
            redisTemplate.opsForHash().putAll(redisShareStatusKey, getShareStatusMap(event));
            redisTemplate.expire(redisShareStatusKey, Duration.ofDays(10L));
        }
        boolean isNew = redisTemplate.hasKey(redisShareSetKey);
        redisTemplate.opsForSet().add(redisShareSetKey, event.getCode());
        if (isNew) {
            redisTemplate.expire(redisShareSetKey, Duration.ofDays(10L));
        }
    }

    private Map<String, String> getShareStatusMap(SinaStockMarketSaveEvent stockMarket) {
        Map<String, String> shareInfo = Maps.newHashMap();
        shareInfo.put(ShareInfoCacheValue.HASH_CODE, stockMarket.getCode());
        shareInfo.put(ShareInfoCacheValue.HASH_NAME, stockMarket.getName());
        shareInfo.put(ShareInfoCacheValue.HASH_EXCHANGE, ShareExchange.getExchange(stockMarket.getCode()).getCode());
        shareInfo.put(ShareInfoCacheValue.HASH_BOARD, ShareBoard.getBoard(stockMarket.getCode()).getKey());
        shareInfo.put(ShareInfoCacheValue.HASH_STATUS, ShareStatus.getStatus(stockMarket.getName()).getCode());
        return shareInfo;
    }



    public Set<String> getShareCodes() {
        String redisShareSetKey = LuciferShareConstant.getRedisShareSetKey();
        if (!redisTemplate.hasKey(redisShareSetKey)) {
            return Sets.newHashSet();
        }
        return redisTemplate.opsForSet().members(redisShareSetKey);
    }

    public ShareInfoCacheValue getShareInfoCache(String shareCode) {
        HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
        return new ShareInfoCacheValue(hashOps, shareCode);
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateShareHistoryUpdateDate(String shareCode, int day) {
        if(redisTemplate.hasKey(shareCode)){
            HashOperations<String, String, String> hashOps = redisTemplate.opsForHash();
            hashOps.putIfAbsent(LuciferShareConstant.getRedisShareStatusKey(shareCode), ShareInfoCacheValue.HASH_HISTORY, String.valueOf(day));
        }
    }


    public synchronized List<ShareInfoCacheValue> getAll() {
        Set<String> codes = getShareCodes();
        return codes.stream().map(e -> new ShareInfoCacheValue(redisTemplate.opsForHash(), e)).toList();
    }



}
