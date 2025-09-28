package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.share.service.model.cache.IndexInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.constant.ShareIndexType;
import com.muniu.cloud.lucifer.share.service.model.dto.IndexInfoQueryDTO;
import com.muniu.cloud.lucifer.share.service.model.dto.IndexInfoUpdateDTO;
import com.muniu.cloud.lucifer.share.service.entity.IndexInfo;
import com.muniu.cloud.lucifer.share.service.mapper.IndexInfoMapper;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 指数基本信息服务
 */
@Service
@Slf4j
public class IndexInfoService extends ServiceImpl<IndexInfoMapper, IndexInfo> {

    private static final byte DF = 0;
    
    // 指数缓存，key为指数代码，value为缓存值对象
    private final Map<String, IndexInfoCacheValue> indexCache = new ConcurrentHashMap<>();
    private final TradingDateTimeService tradingDayService;
    private final AkToolsService akToolsService;

    @Autowired
    public IndexInfoService(AkToolsService akToolsService, TradingDateTimeService tradingDayService) {
        this.akToolsService = akToolsService;
        this.tradingDayService = tradingDayService;
    }
    
    /**
     * 初始化缓存，从数据库加载指数信息
     */
    @PostConstruct
    @Transactional
    public void initCache() {
        try {
            // 从数据库加载所有指数信息
            List<IndexInfo> indexInfoList = getBaseMapper().selectList(null);
            if (CollectionUtils.isNotEmpty(indexInfoList)) {
                updateCache(indexInfoList);
                log.info("初始化指数缓存成功，共{}条", indexInfoList.size());
            } else {
                log.info("数据库中未找到指数信息，尝试立即同步指数数据");
                // 尝试同步数据
                syncIndexInfoData();
            }
        } catch (Exception e) {
            log.error("初始化指数缓存失败", e);
        }
    }
    
    /**
     * 更新缓存
     * @param indexInfoList 指数信息列表
     */
    private void updateCache(List<IndexInfo> indexInfoList) {
        if (CollectionUtils.isEmpty(indexInfoList)) {
            return;
        }
        // 清空原有缓存
        indexCache.clear();
        indexInfoList.stream().map(IndexInfoCacheValue::new).forEach(e-> indexCache.put(e.getIndexCode(), e));
        log.info("更新指数缓存成功，当前缓存大小: {}", indexCache.size());
    }
    
    /**
     * 根据指数代码获取指数缓存
     * @param indexCode 指数代码
     * @return 指数缓存值，不存在返回null
     */
    public IndexInfoCacheValue getIndexCache(String indexCode) {
        if (StringUtils.isBlank(indexCode)) {
            return null;
        }
        return indexCache.get(indexCode);
    }
    

    /**
     * 获取所有指数缓存
     * @return 所有指数缓存值列表
     */
    public List<IndexInfoCacheValue> getAllIndexCache() {
        return new ArrayList<>(indexCache.values());
    }
    
    /**
     * 根据指数来源获取指数缓存
     * @param source 指数来源
     * @return 符合条件的指数缓存值列表
     */
    public List<IndexInfoCacheValue> getIndexCacheBySource(String source) {
        if (StringUtils.isBlank(source)) {
            return new ArrayList<>();
        }
        return indexCache.values().stream()
            .filter(cache -> source.equals(cache.getSource()))
            .collect(Collectors.toList());
    }
    
    /**
     * 判断指数代码是否在缓存中
     * @param indexCode 指数代码
     * @return 是否在缓存中
     */
    public boolean containsIndex(String indexCode) {
        return StringUtils.isNotBlank(indexCode) && indexCache.containsKey(indexCode);
    }
    

    /**
     * 同步指数信息数据
     * 每个交易日早上8点执行
     *
     */
    @Scheduled(cron = "0 0 8 * * ?")
    @Transactional(rollbackFor = Exception.class)
    protected void syncIndexInfoData() {
        try {
            // 获取指数列表数据
            List<IndexInfo> indexInfoList = getIndexInfoList();
            if (CollectionUtils.isNotEmpty(indexInfoList)) {
                // 保存指数列表数据
                getBaseMapper().batchInsertOrUpdate(indexInfoList);
                // 更新缓存
                updateCache(indexInfoList);
            } else {
                log.warn("未获取到指数信息数据");
            }
        } catch (Exception e) {
            log.error("同步指数信息数据异常", e);
        }
    }


    @Transactional(rollbackFor = Exception.class)
    public void updateHistUpdate(String indexCode, Integer indexHistUpdate){
        getBaseMapper().updateHistUpdate(indexCode, indexHistUpdate);
        IndexInfoCacheValue cacheValue = indexCache.get(indexCode);
        if(cacheValue != null){
            cacheValue.setIndexHistUpdate(indexHistUpdate);
        }
    }

    /**
     * 获取指数列表数据
     * @return 指数列表数据
     * @throws Exception 异常
     */
    public List<IndexInfo> getIndexInfoList() throws Exception {
        JSONArray baseIndexStockArray = JSON.parseArray(akToolsService.indexStockInfo());
        Map<String,Integer> baseIndexStockMap = baseIndexStockArray.stream().collect(Collectors.toMap((e)->((JSONObject)e).getString("index_code"),e->Integer.valueOf(((JSONObject)e).getString("publish_date").replace("-",""))));
        int tradingDay = tradingDayService.getLstTradingDay();
        List<IndexInfo> result = Lists.newArrayList();
        for (ShareIndexType indexType : ShareIndexType.values()) {
            String jsonData = akToolsService.stockZhIndexSpotEm(indexType.getName());
            if (StringUtils.isBlank(jsonData)) {
                continue;
            }
            long currentTime = System.currentTimeMillis();
            JSONArray array = JSON.parseArray(jsonData);
            List<IndexInfo> indexInfoList = new ArrayList<>();
            for (int i = 0; i < array.size(); i++) {
                JSONObject jsonObject = array.getJSONObject(i);
                String indexCode = jsonObject.getString("代码");
                IndexInfo indexInfo = new IndexInfo();
                indexInfo.setIndexCode(indexCode);
                indexInfo.setDisplayName(jsonObject.getString("名称"));
                indexInfo.setSource(indexType.getCode());
                indexInfo.setPublishDate(baseIndexStockMap.get(indexCode) == null ? 0 : baseIndexStockMap.get(indexCode));
                indexInfo.setUpdateDate(tradingDay);
                indexInfo.setCreateTime(currentTime);
                indexInfo.setUpdateTime(currentTime);
                indexInfo.setUpdateConstituent(DF);
                indexInfo.setUpdateHistory(DF);
                result.add(indexInfo);
            }
        }
        return result;
    }

    /**
     * 根据查询条件分页查询指数信息
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public List<IndexInfo> queryIndexInfoPage(IndexInfoQueryDTO queryDTO) {
        log.info("分页查询指数信息，参数: {}", JSON.toJSONString(queryDTO));
        // 构建查询条件
        LambdaQueryWrapper<IndexInfo> queryWrapper = new LambdaQueryWrapper<>();
        // 按条件查询
        if (StringUtils.isNotBlank(queryDTO.getIndexCode())) {
            queryWrapper.like(IndexInfo::getIndexCode, queryDTO.getIndexCode());
        }
        if (StringUtils.isNotBlank(queryDTO.getDisplayName())) {
            queryWrapper.like(IndexInfo::getDisplayName, queryDTO.getDisplayName());
        }
        if (StringUtils.isNotBlank(queryDTO.getSource())) {
            queryWrapper.eq(IndexInfo::getSource, queryDTO.getSource());
        }
        // 默认按更新时间降序排序
        queryWrapper.orderByDesc(IndexInfo::getUpdateTime);
        return getBaseMapper().selectList(queryWrapper);
    }
    
    /**
     * 更新指数的来源、更新历史数据状态和是否更新成分股状态
     * @param updateDTO 更新数据
     * @return 是否更新成功
     */
    @Transactional(rollbackFor = Exception.class)
    public boolean updateIndexSettings(IndexInfoUpdateDTO updateDTO) {
        log.info("更新指数设置，参数: {}", JSON.toJSONString(updateDTO));
        // 检查指数是否存在
        IndexInfo indexInfo = getBaseMapper().selectOne(new QueryWrapper<IndexInfo>().eq("index_code",updateDTO.getIndexCode()));
        if (indexInfo == null || StringUtils.isBlank(indexInfo.getIndexCode())) {
            log.error("更新失败: 指数[{}]不存在", updateDTO.getIndexCode());
            return false;
        }
        indexInfo.setUpdateHistory(updateDTO.getUpdateHistory());
        indexInfo.setUpdateConstituent(updateDTO.getUpdateConstituent());
        indexInfo.setSource(updateDTO.getSource());
        // 执行更新
        int rows = getBaseMapper().updateIndexSettings(updateDTO.getIndexCode(), updateDTO.getSource(), updateDTO.getUpdateHistory(), updateDTO.getUpdateConstituent(), System.currentTimeMillis());
        indexCache.put(updateDTO.getIndexCode(),new IndexInfoCacheValue(indexInfo));
        return rows > 0;
    }

} 