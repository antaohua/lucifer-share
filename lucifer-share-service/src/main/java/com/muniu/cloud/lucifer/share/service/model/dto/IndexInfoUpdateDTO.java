package com.muniu.cloud.lucifer.share.service.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 指数信息更新DTO
 */
@Data
@Schema(description = "指数信息更新DTO")
public class IndexInfoUpdateDTO {

    /**
     * 指数代码
     */
    @NotBlank(message = "指数代码不能为空")
    @Schema(description = "指数代码", example = "000001")
    private String indexCode;
    
    /**
     * 指数来源
     */
    @Schema(description = "指数来源", example = "SH")
    private String source;
    
    /**
     * 是否更新历史数据 1:更新 0:不更新
     */
    @NotNull(message = "是否更新历史数据状态不能为空")
    @Schema(description = "是否更新历史数据：1-更新 0-不更新", example = "1")
    private Byte updateHistory;
    
    /**
     * 是否更新成分股 1:更新 0:不更新
     */
    @Schema(description = "是否更新成分股：1-更新 0-不更新", example = "1")
    private Byte updateConstituent;

} 