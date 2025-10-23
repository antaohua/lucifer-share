package com.muniu.cloud.lucifer.share.service.entity;



import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseCustomIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "share_indices")
@Data
public class ShareIndexEntity extends BaseCustomIdEntity implements JpaCreateColumn {

    private long createTime;

    @Column(name = "indices_name", length = 100, nullable = false,comment = "指数名称")
    private String indicesName;

    @Column(name = "indices_type", length = 50, nullable = false)
    private String indicesType;

    @Column(name = "indices_date")
    private Integer indicesDate;

}
