package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.ToString;

/**
 * 交易日实体类
 */
@TableName("trading_day")
@Data
@ToString
public class TradingDay {

    /**
     * 交易日（格式：yyyyMMdd），如20240101
     */
    @TableId(type = IdType.INPUT)
    private Integer day;

    /**
     * 数据创建时间
     */
    private Long createTime;

    /**
     * 数据更新时间
     */
    private Long updateTime;


    public TradingDay(Integer day, Long createTime, Long updateTime) {
        this.day = day;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }

    public TradingDay() {
    }
}