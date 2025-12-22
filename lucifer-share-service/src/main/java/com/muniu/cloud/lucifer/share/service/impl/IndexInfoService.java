package com.muniu.cloud.lucifer.share.service.impl;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;
import com.muniu.cloud.lucifer.commons.model.constants.Condition;
import com.muniu.cloud.lucifer.commons.model.constants.Operator;
import com.muniu.cloud.lucifer.commons.model.page.PageParams;
import com.muniu.cloud.lucifer.commons.model.page.PageResult;
import com.muniu.cloud.lucifer.share.service.dao.TradeIndexDao;
import com.muniu.cloud.lucifer.share.service.model.cache.IndexInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.model.dto.IndexInfoQueryDTO;
import com.muniu.cloud.lucifer.share.service.model.dto.IndexInfoUpdateDTO;
import com.muniu.cloud.lucifer.share.service.entity.TradeIndexEntity;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
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
public class IndexInfoService {
    
    // 指数缓存，key为指数代码，value为缓存值对象
    private final Map<String, IndexInfoCacheValue> indexCache = new ConcurrentHashMap<>();
    private final TradeIndexDao tradeIndexDao;

    @Autowired
    public IndexInfoService(TradeIndexDao tradeIndexDao) {
        this.tradeIndexDao = tradeIndexDao;
    }

    /**
     * 初始化缓存，从数据库加载指数信息
     */
    @PostConstruct
    @Transactional
    public void initCache() {
        try {
            // 从数据库加载所有指数信息
            List<TradeIndexEntity> tradeIndexEntityList = tradeIndexDao.getAll(false);
            if (CollectionUtils.isNotEmpty(tradeIndexEntityList)) {
                updateCache(tradeIndexEntityList);
                log.info("初始化指数缓存成功，共{}条", tradeIndexEntityList.size());
            }
        } catch (Exception e) {
            log.error("初始化指数缓存失败", e);
        }
    }
    
    /**
     * 更新缓存
     * @param tradeIndexEntityList 指数信息列表
     */
    private void updateCache(List<TradeIndexEntity> tradeIndexEntityList) {
        if (CollectionUtils.isEmpty(tradeIndexEntityList)) {
            return;
        }
        // 清空原有缓存
        indexCache.clear();
        tradeIndexEntityList.stream().map(IndexInfoCacheValue::new).forEach(e-> indexCache.put(e.getIndexCode(), e));
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
    



    @Transactional(rollbackFor = Exception.class)
    public void updateHistUpdate(String indexCode, Integer indexHistUpdate){
        tradeIndexDao.updateHistUpdate(indexCode, indexHistUpdate);
        IndexInfoCacheValue cacheValue = indexCache.get(indexCode);
        if(cacheValue != null){
            cacheValue.setIndexHistUpdate(indexHistUpdate);
        }
    }


    /**
     * 根据查询条件分页查询指数信息
     * @param queryDTO 查询条件
     * @return 分页结果
     */
    public PageResult<TradeIndexEntity> queryIndexInfoPage(IndexInfoQueryDTO queryDTO) {
        log.info("分页查询指数信息，参数: {}", JSON.toJSONString(queryDTO));
        // 构建查询条件
        List<Condition> conditions = Lists.newArrayList();
        // 按条件查询
        if (StringUtils.isNotBlank(queryDTO.getIndexCode())) {
            conditions.add(new Condition("id", Operator.LIKE, queryDTO.getIndexCode()));
        }
        if (StringUtils.isNotBlank(queryDTO.getDisplayName())) {
            conditions.add(new Condition("displayName", Operator.LIKE, queryDTO.getDisplayName()));
        }
        if (StringUtils.isNotBlank(queryDTO.getSource())) {
            conditions.add(new Condition("source", queryDTO.getSource()));
        }
        PageParams pageParams = new PageParams(queryDTO.getPageNum(), queryDTO.getPageSize(),conditions);
        return tradeIndexDao.getByPage(pageParams, true);
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
        TradeIndexEntity tradeIndexEntity = tradeIndexDao.getById(updateDTO.getIndexCode());
        if (tradeIndexEntity == null || StringUtils.isBlank(tradeIndexEntity.getId())) {
            log.error("更新失败: 指数[{}]不存在", updateDTO.getIndexCode());
            return false;
        }
        tradeIndexEntity.setUpdateHistory(updateDTO.getUpdateHistory());
        tradeIndexEntity.setUpdateConstituent(updateDTO.getUpdateConstituent());
        tradeIndexEntity.setSource(updateDTO.getSource());
        // 执行更新
        Map<String,Object> pramMap = Map.of("source",updateDTO.getSource(),
                "updateHistory",updateDTO.getUpdateHistory(),
                "updateConstituent",updateDTO.getUpdateConstituent());
        indexCache.put(updateDTO.getIndexCode(),new IndexInfoCacheValue(tradeIndexEntity));
        return tradeIndexDao.updateById(updateDTO.getIndexCode(),pramMap)>0;
    }

} 