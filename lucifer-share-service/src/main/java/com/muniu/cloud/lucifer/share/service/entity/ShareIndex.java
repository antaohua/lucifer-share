package com.muniu.cloud.lucifer.share.service.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.ToString;

@Entity
@Table(name = "share_indices")
@Data
@TableName("share_indices")
public class ShareIndex {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    @TableId(value = "id", type = IdType.INPUT)
    private String id;

    @Column(name = "indices_name", length = 100, nullable = false)
    private String indicesName;

    @Column(name = "indices_type", length = 50, nullable = false)
    private String indicesType;

    @Column(name = "indices_date")
    private Integer indicesDate;

    @Column(name = "create_time", nullable = false)
    private Long createTime;
}
