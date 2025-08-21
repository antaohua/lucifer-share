package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 指数基本信息
 */
@Data
@TableName("index_info")
@Schema(description = "指数基本信息")
public class IndexInfo {

    @TableId(value = "index_code", type = IdType.INPUT)
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
     * 创建时间
     */
    @Schema(description = "创建时间（毫秒时间戳）")
    private Long createTime;
    
    /**
     * 更新时间
     */
    @Schema(description = "更新时间（毫秒时间戳）")
    private Long updateTime;

    /**
     * 指数历史数据最后更新时间（毫秒时间戳）
     */
    @Schema(description = "指数历史数据最后更新日期", example = "20230601")
    private Integer indexHistUpdate;
    
    /**
     * 指数成分股最后更新时间（毫秒时间戳）
     */
    @Schema(description = "指数成分股最后更新日期", example = "20230601")
    private Integer indexConstUpdate;
    
    /**
     * 是否更新历史数据 1:更新 0:不更新
     */
    @Schema(description = "是否更新历史数据：1-更新 0-不更新", example = "1")
    private Byte updateHistory;
    
    /**
     * 是否更新成分股 1:更新 0:不更新
     */
    @Schema(description = "是否更新成分股：1-更新 0-不更新", example = "1")
    private Byte updateConstituent;

}