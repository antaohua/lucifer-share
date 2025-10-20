package com.muniu.cloud.lucifer.share.service.entity;



import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseCustomIdEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "share_indices")
@Data
public class ShareIndexEntity extends BaseCustomIdEntity {


    @Column(name = "indices_name", length = 100, nullable = false)
    private String indicesName;

    @Column(name = "indices_type", length = 50, nullable = false)
    private String indicesType;

    @Column(name = "indices_date")
    private Integer indicesDate;

    @Column(name = "create_time", nullable = false)
    private Long createTime;
}
