package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Data
@TableName("td_stock_real_Time_")
public class TdStockRealTime {

    /**
     * {code}_250101_140221
     * 股票的唯一标识
     */
    @TableId(value = "id", type = IdType.INPUT)
    private String id;  // 股票唯一标识符

    /**
     * 股票代码
     * 例如：sz000001
     */
    private String code;  // 股票代码

    /**
     * 交易时间
     * 对应 MySQL 的 TIME 类型
     */
    private LocalTime tickTime;

    /**
     * 交易日期
     * 对应 MySQL 的 DATE 类型
     */
    private LocalDate tickDate;  // 交易日期

    /**
     * 当前价格
     * 股票的最新成交价格
     */
    private BigDecimal price;  // 当前价格

    /**
     * 交易量
     * 表示交易的股票数量，单位：股
     */
    private long volume;  // 交易量 (股)

    /**
     * 前一个价格
     * 上一个交易的价格
     */
    private BigDecimal prevPrice;  // 前一个价格

    /**
     * 类型
     * 'D' 表示卖盘，其他表示买盘
     */
    private String kind;  // 类型：'D' 表示卖盘，其他表示买盘
}
