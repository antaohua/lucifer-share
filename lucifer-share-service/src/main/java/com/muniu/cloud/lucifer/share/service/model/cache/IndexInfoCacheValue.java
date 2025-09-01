package com.muniu.cloud.lucifer.share.service.model.cache;

import com.muniu.cloud.lucifer.share.service.entity.IndexInfo;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * 指数信息缓存对象
 * @author antaohua
 */
@Schema(description = "指数信息缓存对象")
public class IndexInfoCacheValue {

    /**
     * 指数代码
     */
    @Schema(description = "指数代码", example = "000001")
    private String indexCode;
    
    /**
     * 指数名称
     */
    @Schema(description = "指数名称", example = "上证指数")
    private String displayName;
    
    /**
     * 发布日期 格式：yyyyMMdd
     */
    @Schema(description = "发布日期，格式：yyyyMMdd", example = "19901219")
    private Integer publishDate;
    
    /**
     * 指数来源
     */
    @Schema(description = "指数来源", example = "SH")
    private String source;
    
    /**
     * 更新日期 格式：yyyyMMdd
     */
    @Schema(description = "更新日期，格式：yyyyMMdd", example = "20230601")
    private Integer updateDate;
    
    /**
     * 是否更新成分股 1:更新 0:不更新
     */
    @Schema(description = "是否更新成分股：1-更新 0-不更新", example = "1")
    private Byte updateConstituent;

    /**
     * 是否更新历史数据 1:更新 0:不更新
     */
    @Schema(description = "是否更新历史数据：1-更新 0-不更新", example = "1")
    private Byte updateHistory;
    
    /**
     * 指数历史数据最后更新时间（毫秒时间戳）
     */
    @Schema(description = "指数历史数据最后更新日期",example = "19901219")
    private Integer indexHistUpdate;
    
    /**
     * 指数成分股最后更新时间（毫秒时间戳）
     */
    @Schema(description = "指数成分股最后更新日期",example = "19901219")
    private Integer indexConstUpdate;
    
    public IndexInfoCacheValue() {
    }

    public IndexInfoCacheValue(IndexInfo indexInfo) {
        this.indexCode = indexInfo.getIndexCode();
        this.displayName = indexInfo.getDisplayName();
        this.publishDate = indexInfo.getPublishDate();
        this.source = indexInfo.getSource();
        this.updateDate = indexInfo.getUpdateDate();
        this.updateConstituent = indexInfo.getUpdateConstituent();
        this.updateHistory = indexInfo.getUpdateHistory();
        this.indexHistUpdate = indexInfo.getIndexHistUpdate();
        this.indexConstUpdate = indexInfo.getIndexConstUpdate();
    }

    public IndexInfoCacheValue(String indexCode, String displayName, Integer publishDate, String source, Integer updateDate, Byte updateConstituent, Byte updateHistory, Integer indexHistUpdate, Integer indexConstUpdate) {
        this.indexCode = indexCode;
        this.displayName = displayName;
        this.publishDate = publishDate;
        this.source = source;
        this.updateDate = updateDate;
        this.updateConstituent = updateConstituent;
        this.updateHistory = updateHistory;
        this.indexHistUpdate = indexHistUpdate;
        this.indexConstUpdate = indexConstUpdate;
    }

    public String getIndexCode() {
        return indexCode;
    }
    
    public void setIndexCode(String indexCode) {
        this.indexCode = indexCode;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public Integer getPublishDate() {
        return publishDate;
    }
    
    public void setPublishDate(Integer publishDate) {
        this.publishDate = publishDate;
    }
    
    public String getSource() {
        return source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    
    public Integer getUpdateDate() {
        return updateDate;
    }
    
    public void setUpdateDate(Integer updateDate) {
        this.updateDate = updateDate;
    }

    public Byte getUpdateConstituent() {
        return updateConstituent;
    }

    public void setUpdateConstituent(Byte updateConstituent) {
        this.updateConstituent = updateConstituent;
    }

    public Byte getUpdateHistory() {
        return updateHistory;
    }

    public void setUpdateHistory(Byte updateHistory) {
        this.updateHistory = updateHistory;
    }

    public Integer getIndexHistUpdate() {
        return indexHistUpdate;
    }

    public void setIndexHistUpdate(Integer indexHistUpdate) {
        this.indexHistUpdate = indexHistUpdate;
    }

    public Integer getIndexConstUpdate() {
        return indexConstUpdate;
    }

    public void setIndexConstUpdate(Integer indexConstUpdate) {
        this.indexConstUpdate = indexConstUpdate;
    }

    public IndexInfo toIndexInfo() {
        IndexInfo indexInfo = new IndexInfo();
        indexInfo.setIndexCode(indexCode);
        indexInfo.setDisplayName(displayName);
        indexInfo.setPublishDate(publishDate);
        indexInfo.setSource(source);
        indexInfo.setUpdateDate(updateDate);
        indexInfo.setUpdateConstituent(updateConstituent);
        indexInfo.setUpdateHistory(updateHistory);
        indexInfo.setIndexHistUpdate(indexHistUpdate);
        indexInfo.setIndexConstUpdate(indexConstUpdate);
        return indexInfo;
    }
}