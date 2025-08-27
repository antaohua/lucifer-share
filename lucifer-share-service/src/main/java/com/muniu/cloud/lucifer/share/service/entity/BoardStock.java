package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;

/**
 * 概念板块成份股关系实体类
 */
@Entity
@Table(name = "share_board_stock")
@TableName("share_board_stock")
@Data
@ToString
public class BoardStock {
    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 概念板块代码
     */
    @Column(name = "board_code" ,length = 10, nullable = false)
    private String boardCode;
    
    /**
     * 股票代码
     */
    @Column(name = "stock_code" ,length = 10, nullable = false)
    private String stockCode;
    
    /**
     * 是否有效
     */
    @Column(name = "is_valid" , nullable = false)
    private Boolean isValid;
    
    /**
     * 创建时间（毫秒时间戳）
     */
    @Column(name = "create_time", nullable = false)
    private Long createTime;
    
    /**
     * 更新时间（毫秒时间戳）
     */
    @Column(name = "update_time")
    private Long updateTime;

    public BoardStock(Long id, String boardCode, String stockCode, Boolean isValid, Long createTime, Long updateTime) {
        this.id = id;
        this.boardCode = boardCode;
        this.stockCode = stockCode;
        this.isValid = isValid;
        this.createTime = createTime;
        this.updateTime = updateTime;
    }


    public BoardStock(String boardCode, String stockCode, Long createTime) {
        this.boardCode = boardCode;
        this.stockCode = stockCode;
        this.createTime = createTime;
        this.updateTime = createTime;
        this.isValid = true;
    }

    public BoardStock() {
    }
}