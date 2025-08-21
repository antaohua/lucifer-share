package com.muniu.cloud.lucifer.share.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 指数信息查询DTO
 */
@Schema(description = "指数信息查询DTO")
@Data
public class IndexInfoQueryDTO {

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
     * 指数来源
     */
    @Schema(description = "指数来源", example = "SH")
    private String source;
    
    /**
     * 是否更新历史数据 1:更新 0:不更新
     */
    @Schema(description = "是否更新历史数据：1-更新 0-不更新", example = "1")
    private Integer updateHistory;
    
    /**
     * 是否更新成分股 1:更新 0:不更新
     */
    @Schema(description = "是否更新成分股：1-更新 0-不更新", example = "1")
    private Integer updateConstituent;
    
    /**
     * 页码
     */
    @Schema(description = "页码", example = "1", defaultValue = "1")
    private Integer pageNum = 1;
    
    /**
     * 每页条数
     */
    @Schema(description = "每页条数", example = "10", defaultValue = "10")
    private Integer pageSize = 10;
} 