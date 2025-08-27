package com.muniu.cloud.lucifer.share.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.muniu.cloud.lucifer.commons.model.page.PageParams;
import com.muniu.cloud.lucifer.commons.model.page.PageResult;
import com.muniu.cloud.lucifer.share.service.cache.ShareInfoCacheValue;
import com.muniu.cloud.lucifer.share.service.constant.ShareStatus;
import com.muniu.cloud.lucifer.share.service.entity.ShareInfo;
import com.muniu.cloud.lucifer.share.service.mapper.ShareInfoMapper;
import com.muniu.cloud.lucifer.share.service.model.SimpleShareInfo;
import com.muniu.cloud.lucifer.share.service.utils.SpringContextUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static com.muniu.cloud.lucifer.commons.utils.constants.DateConstant.DATE_FORMATTER_YYYYMMDD;

@Service
@Slf4j
public class ShareInfoService extends ServiceImpl<ShareInfoMapper,ShareInfo> {


    private static final ConcurrentHashMap<String, ShareInfoCacheValue> SHARE_INFO_CACHE = new ConcurrentHashMap<>();

    private final TradingDayService tradingDayService;
    private final AkToolsService akToolsService;



    @Autowired
    public ShareInfoService(ShareInfoMapper shareInfoMapper, TradingDayService tradingDayService, AkToolsService akToolsService) {
        this.tradingDayService = tradingDayService;
        this.akToolsService = akToolsService;
    }


    public List<ShareInfo> getByShareCodes(List<String> shareCodes) {
        return getBaseMapper().selectByIds(shareCodes);
    }

    public Set<String> getShareCodes() {
        if (MapUtils.isEmpty(SHARE_INFO_CACHE)) {
            loadCache();
        }
        return Sets.newHashSet(SHARE_INFO_CACHE.keySet());
    }

    public ShareInfoCacheValue getShareInfoCache(String shareCode) {
        if (MapUtils.isEmpty(SHARE_INFO_CACHE)) {
            loadCache();
        }
        return SHARE_INFO_CACHE.get(shareCode);
    }

    public Map<String, ShareInfoCacheValue> getALL() {
        if (MapUtils.isEmpty(SHARE_INFO_CACHE)) {
            loadCache();
        }
        return Maps.newHashMap(SHARE_INFO_CACHE);
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateShareHistoryUpdateDate(String shareCode, int day) {
        if (MapUtils.isEmpty(SHARE_INFO_CACHE)) {
            loadCache();
        }
        ShareInfoCacheValue shareInfoCacheValue = SHARE_INFO_CACHE.get(shareCode);
        if (shareInfoCacheValue != null) {
            getBaseMapper().updateShareHistoryUpdateDate(shareCode, day);
            shareInfoCacheValue.setHistoryUpdateDate(day);
        }
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateShareHistoryUpdateDate(List<String> shareCodes, int day) {
        if (MapUtils.isEmpty(SHARE_INFO_CACHE)) {
            loadCache();
        }
        getBaseMapper().updateShareHistoryUpdateDateBatch(shareCodes, day);


        for (String shareCode : shareCodes) {
            SHARE_INFO_CACHE.computeIfPresent(shareCode, (k, v) -> {
                v.setHistoryUpdateDate(day);
                return v;
            });
        }
    }


    public void loadCache() {
        List<ShareInfo> shareInfoEntities = getBaseMapper().selectList(new QueryWrapper<>());
        if(CollectionUtils.isEmpty(shareInfoEntities)) {
            try {
                SpringContextUtils.getBean(ShareInfoService.class).shanghaiShareList();
                shareInfoEntities = getBaseMapper().selectList(new QueryWrapper<>());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        Map<String, ShareInfoCacheValue> shareInfoCacheValueMap = shareInfoEntities.stream().collect(Collectors.toMap(ShareInfo::getId, ShareInfoCacheValue::new));
        SHARE_INFO_CACHE.clear();
        SHARE_INFO_CACHE.putAll(shareInfoCacheValueMap);
    }


    @Transactional(rollbackFor = Exception.class)
    @Scheduled(cron = "0 0 01 * * ?")
    public void shanghaiShareList() throws IOException {

        int day = Integer.parseInt(LocalDate.now().format(DATE_FORMATTER_YYYYMMDD));
        if (!tradingDayService.isTradingDay(day)) {
            return;
        }
        List<SimpleShareInfo> shanghai = akToolsService.stockInfoShNameCode();
        List<SimpleShareInfo> shenzhen = akToolsService.stockInfoSzNameCode();
        List<SimpleShareInfo> beijing = akToolsService.stockInfoBjNameCode();
        List<SimpleShareInfo> shareInfos = Lists.newArrayList();
        shareInfos.addAll(shanghai);
        shareInfos.addAll(shenzhen);
        shareInfos.addAll(beijing);
        long time = System.currentTimeMillis();
        List<ShareInfo> saveData = shareInfos.stream().map(e -> getShareInfoEntity(e, time)).toList();
        List<String> shareCodes = saveData.stream().map(ShareInfo::getId).toList();
        log.info("shareInfoEntities-size:{}", saveData.size());
        saveData.forEach(e -> e.setCreateTime(time));
        saveData.forEach(e -> e.setUpdateTime(time));
        saveData.forEach(e -> e.setInfoUpdateTime(time));
        saveData.forEach(e -> e.setStatusUpdateTime(time));
        getBaseMapper().insertOrUpdate(saveData, saveData.size());
        List<ShareInfo> demistedList = getBaseMapper().selectList(new QueryWrapper<ShareInfo>().notIn("id", shareCodes));
        demistedList.forEach(e -> {
            e.setShareStatus(ShareStatus.DEMISTED.getCode());
            e.setStatusUpdateTime(System.currentTimeMillis());
        });
        List<ShareInfo> shareInfoEntities = getBaseMapper().selectList(new QueryWrapper<>());
        Map<String, ShareInfoCacheValue> shareInfoCacheValueMap = shareInfoEntities.stream().collect(Collectors.toMap(ShareInfo::getId, ShareInfoCacheValue::new));
        SHARE_INFO_CACHE.clear();
        SHARE_INFO_CACHE.putAll(shareInfoCacheValueMap);
    }

    private ShareInfo getShareInfoEntity(SimpleShareInfo simpleShareInfo, long time) {
        ShareInfo shareInfoEntity = new ShareInfo();
        shareInfoEntity.setExchange(simpleShareInfo.getShareExchange().getCode());
        shareInfoEntity.setSection(simpleShareInfo.getShareBoard().getKey());
        shareInfoEntity.setId(simpleShareInfo.getShareCode());
        shareInfoEntity.setShareName(simpleShareInfo.getShareName());
        shareInfoEntity.setListDate(simpleShareInfo.getListDate());
        shareInfoEntity.setShareStatus(ShareStatus.getStatus(simpleShareInfo.getShareName(), simpleShareInfo.getListDate(), simpleShareInfo.getShareBoard(), tradingDayService).getCode());
        shareInfoEntity.setStatusUpdateTime(time);
        return shareInfoEntity;
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
