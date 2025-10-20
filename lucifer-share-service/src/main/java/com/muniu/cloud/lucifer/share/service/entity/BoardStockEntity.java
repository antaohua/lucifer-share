package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseSnowflakeIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaUpdateCloumn;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

/**
 * 概念板块成份股关系实体类
 */
@Entity
@Table(name = "share_board_stock")
@TableName("share_board_stock")
@Data
@ToString
public class BoardStockEntity extends BaseSnowflakeIdEntity implements JpaCreateColumn, JpaUpdateCloumn {


    private long createTime;

    private long updateTime;

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


    public BoardStockEntity(String boardCode, String stockCode) {
        this.boardCode = boardCode;
        this.stockCode = stockCode;
        this.isValid = true;
    }

    public BoardStockEntity() {
    }
}