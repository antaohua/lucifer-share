package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.ToString;

/**
 * 交易日实体类
 */
@Entity
@Data
@ToString
@TableName("trading_day")
@Table(name ="trading_day")
public class TradingDay {

    /**
     * 交易日（格式：yyyyMMdd），如20240101
     */
    @Id
    @Column(name = "day", nullable = false)
    @TableId(type = IdType.INPUT)
    private Integer day;

    /**
     * 数据创建时间
     */
    /**
     * 创建时间（毫秒时间戳）
     */
    @Column(name = "create_time", nullable = false)
    private Long createTime;

    /**
     * 数据更新时间
     */
    @Column(name = "update_time", nullable = false)
    private Long updateTime;
}