package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 概念板块成份股关系实体类
 */
@TableName("share_board_stock")
@Data
public class BoardStock {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 概念板块代码
     */
    private String boardCode;
    
    /**
     * 股票代码
     */
    private String stockCode;
    
    /**
     * 是否有效
     */
    private Boolean isValid;
    
    /**
     * 创建时间（毫秒时间戳）
     */
    private Long createTime;
    
    /**
     * 更新时间（毫秒时间戳）
     */
    private Long updateTime;

    
    @Override
    public String toString() {
        return "BoardStock{" +
                "id=" + id +
                ", boardCode='" + boardCode + '\'' +
                ", stockCode='" + stockCode + '\'' +
                ", isValid=" + isValid +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }

    public BoardStock() {
    }

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
}