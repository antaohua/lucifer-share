package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseAutoIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaDeleteState;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaUpdateCloumn;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleGroupParams;
import jakarta.persistence.*;
import lombok.*;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;

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
@Table(name = "trade_rule_group")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class TradeRuleGroupEntity extends BaseAutoIdEntity implements JpaCreateColumn, JpaUpdateCloumn, JpaDeleteState {

    @Serial
    private static final long serialVersionUID = 1L;

    private long createTime;

    private long updateTime;

    private boolean deleted;

    @Column(name = "description", length = 500, comment = "规则组描述")
    private String description;

    @Column(name = "user_id", length = 50, nullable = false, comment = "用户ID")
    private String userId;

    @Column(name = "name", nullable = false, comment = "名称")
    private String name;

    //新增构造函数
    public TradeRuleGroupEntity(Long createTime, String userId, SaveRuleGroupParams saveRuleGroupParams) {
        this.createTime = createTime;
        this.deleted = false;
        this.description = StringUtils.isBlank(saveRuleGroupParams.description()) ? "" : saveRuleGroupParams.description();
        this.updateTime = createTime;
        this.userId = userId;
        this.name = saveRuleGroupParams.name();
    }

}
