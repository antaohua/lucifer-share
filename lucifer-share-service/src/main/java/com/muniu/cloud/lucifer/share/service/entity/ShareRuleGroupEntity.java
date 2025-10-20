package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseAutoIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaDeleteState;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaUpdateCloumn;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleGroupParams;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "share_rule_group")
@Data
@ToString
public class ShareRuleGroupEntity extends BaseAutoIdEntity implements JpaCreateColumn, JpaUpdateCloumn, JpaDeleteState {

    @Serial
    private static final long serialVersionUID = 1L;



    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private long createTime;

    /**
     * 更新时间
     */
    @Column(name = "update_time", nullable = false)
    private long updateTime;

    /**
     * 删除标记
     */
    @Column(name = "deleted" , comment = "逻辑删除标记，false 未删除，true 已删除")
    private boolean deleted;




    /**
     * 规则组描述
     */
    @Column(name = "description", length = 500)
    private String description;

    /**
     * 用户ID
     */
    @Column(name = "user_id", length = 50, nullable = false)
    private String userId;

    @Column(name = "name", nullable = false)
    private String name;




    public ShareRuleGroupEntity() {
    }

    //新增构造函数
    public ShareRuleGroupEntity(Long createTime, String userId, SaveRuleGroupParams saveRuleGroupParams) {
        this.createTime = createTime;
        this.deleted = false;
        this.description = StringUtils.isBlank(saveRuleGroupParams.description()) ? "" : saveRuleGroupParams.description();
        this.updateTime = createTime;
        this.userId = userId;
        this.name = saveRuleGroupParams.name();
    }

}
