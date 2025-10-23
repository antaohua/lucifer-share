package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseAutoIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleDateType;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleParams;
import jakarta.persistence.*;
import lombok.*;

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
@Table(name = "share_rule_item")
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ShareRuleItemEntity extends BaseAutoIdEntity implements JpaCreateColumn {

    @Serial
    private static final long serialVersionUID = 1L;

    private long createTime;

    @Column(name = "cron", length = 300, comment = "cron表达式")
    private String cron;

    @Column(name = "rule_group", nullable = false, comment = "规则组")
    private long ruleGroup;

    @Enumerated(EnumType.STRING)
    @Column(name = "data_source", length = 50, nullable = false, comment = "规则数据源")
    private RuleDataSource dataSource;

    @Column(name = "rule_value", length = 1000, nullable = false, comment = "规则值")
    private String ruleValue;

    @Column(name = "sort", nullable = false, comment = "排序")
    private int sort;

    @Enumerated(EnumType.STRING)
    @Column(name = "date_type", length = 50, comment = "日期类型")
    private RuleDateType dateType;

    @Column(name = "date_value", length = 50, comment = "日期值")
    private String dateValue;

    public ShareRuleItemEntity(Long createTime, Long groupId, SaveRuleParams saveRuleParams) {
        this.createTime = createTime;
        this.cron = saveRuleParams.getCron();
        this.ruleGroup = groupId;
        this.dataSource = saveRuleParams.getDataSource();
        this.ruleValue = saveRuleParams.getRuleValue();
        this.sort = saveRuleParams.getSort();
        this.dateType = saveRuleParams.getDateType();
        this.dateValue = saveRuleParams.getDateValue();
    }
}
