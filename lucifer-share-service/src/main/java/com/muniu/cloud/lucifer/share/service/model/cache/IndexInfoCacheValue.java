package com.muniu.cloud.lucifer.share.service.model.cache;

import com.muniu.cloud.lucifer.share.service.entity.TradeIndexEntity;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 指数信息缓存对象
 * @author antaohua
 */
@Setter
@Getter
@Schema(description = "指数信息缓存对象")
@NoArgsConstructor
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


    public IndexInfoCacheValue(TradeIndexEntity tradeIndexEntity) {
        this.indexCode = tradeIndexEntity.getId();
        this.displayName = tradeIndexEntity.getDisplayName();
        this.publishDate = tradeIndexEntity.getPublishDate();
        this.source = tradeIndexEntity.getSource();
        this.updateDate = tradeIndexEntity.getUpdateDate();
        this.updateConstituent = tradeIndexEntity.getUpdateConstituent();
        this.updateHistory = tradeIndexEntity.getUpdateHistory();
        this.indexHistUpdate = tradeIndexEntity.getIndexHistUpdate();
        this.indexConstUpdate = tradeIndexEntity.getIndexConstUpdate();
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

    public TradeIndexEntity toIndexInfo() {
        TradeIndexEntity tradeIndexEntity = new TradeIndexEntity();
        tradeIndexEntity.setId(indexCode);
        tradeIndexEntity.setDisplayName(displayName);
        tradeIndexEntity.setPublishDate(publishDate);
        tradeIndexEntity.setSource(source);
        tradeIndexEntity.setUpdateDate(updateDate);
        tradeIndexEntity.setUpdateConstituent(updateConstituent);
        tradeIndexEntity.setUpdateHistory(updateHistory);
        tradeIndexEntity.setIndexHistUpdate(indexHistUpdate);
        tradeIndexEntity.setIndexConstUpdate(indexConstUpdate);
        return tradeIndexEntity;
    }
}