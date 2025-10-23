package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseSnowflakeIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaUpdateCloumn;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "share_board_stock", comment = "概念板块成份股关系表", indexes = {
        @Index(columnList = "board_code,stock_code", name = "idx_board_stock", unique = true, options = "USING BTREE"),
        @Index(columnList = "stock_code", name = "idx_stock")
})
@Data
public class BoardStockEntity extends BaseSnowflakeIdEntity implements JpaCreateColumn, JpaUpdateCloumn {

    private long createTime;

    private long updateTime;

    @Column(name = "board_code" ,length = 10, nullable = false, comment = "概念板块代码")
    private String boardCode;

    @Column(name = "stock_code" ,length = 10, nullable = false, comment = "股票代码")
    private String stockCode;

    @Column(name = "is_valid" , nullable = false, comment = "是否有效")
    private Boolean isValid;


    public BoardStockEntity(String boardCode, String stockCode) {
        this.boardCode = boardCode;
        this.stockCode = stockCode;
        this.isValid = true;
    }

    public BoardStockEntity() {
    }
}