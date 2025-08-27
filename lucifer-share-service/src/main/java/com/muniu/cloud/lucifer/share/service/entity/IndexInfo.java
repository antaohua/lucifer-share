package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

/**
 * 指数基本信息
 */
@Entity
@Table(name = "index_info")
@Data
@TableName("index_info")
@Schema(description = "指数基本信息")
public class IndexInfo {

    @Id
    @Column(name = "index_code", length = 10, nullable = false)
    @TableId(value = "index_code", type = IdType.INPUT)
    @Schema(description = "指数代码", example = "000001")
    private String indexCode;
    
    /**
     * 指数名称
     */
    @Column(name = "display_name", length = 100, nullable = false)
    @Schema(description = "指数名称", example = "上证指数")
    private String displayName;
    
    /**
     * 发布日期 格式：yyyyMMdd
     */
    @Column(name = "publish_date", nullable = false)
    @Schema(description = "发布日期，格式：yyyyMMdd", example = "19901219")
    private Integer publishDate;
    
    /**
     * 指数来源
     */
    @Column(name = "source", length = 10, nullable = false)
    @Schema(description = "指数来源", example = "SH")
    private String source;
    
    /**
     * 更新日期 格式：yyyyMMdd
     */
    @Column(name = "update_date", nullable = false)
    @Schema(description = "更新日期，格式：yyyyMMdd", example = "20230601")
    private Integer updateDate;
    
    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    @Schema(description = "创建时间（毫秒时间戳）")
    private Long createTime;
    
    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    @Schema(description = "更新时间（毫秒时间戳）")
    private Long updateTime;

    /**
     * 指数历史数据最后更新时间（毫秒时间戳）
     */
    @Column(name = "index_hist_update")
    @Schema(description = "指数历史数据最后更新日期", example = "20230601")
    private Integer indexHistUpdate;
    
    /**
     * 指数成分股最后更新时间（毫秒时间戳）
     */
    @Column(name = "index_const_update")
    @Schema(description = "指数成分股最后更新日期", example = "20230601")
    private Integer indexConstUpdate;
    
    /**
     * 是否更新历史数据 1:更新 0:不更新
     */
    @Column(name = "update_history", nullable = false)
    @Schema(description = "是否更新历史数据：1-更新 0-不更新", example = "1")
    private Byte updateHistory;
    
    /**
     * 是否更新成分股 1:更新 0:不更新
     */
    @Column(name = "update_constituent", nullable = false)
    @Schema(description = "是否更新成分股：1-更新 0-不更新", example = "1")
    private Byte updateConstituent;

}