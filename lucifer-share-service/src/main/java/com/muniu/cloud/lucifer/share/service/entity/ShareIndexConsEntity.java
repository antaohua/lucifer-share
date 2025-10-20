package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;


@Entity
@Table(name = "share_index_cons")
@Data
@TableName("share_index_cons")
public class ShareIndexConsEntity {

    @Id
    @Column(name = "id", length = 50, nullable = false)
    private String id;

    @Column(name = "index_code", length = 10, nullable = false)
    private String indexCode;

    @Column(name = "share_code", length = 10, nullable = false)
    private String shareCode;

    @Column(name = "date", nullable = false)
    private Integer date;

    @Column(name = "status", nullable = false)
    private byte status;
}
