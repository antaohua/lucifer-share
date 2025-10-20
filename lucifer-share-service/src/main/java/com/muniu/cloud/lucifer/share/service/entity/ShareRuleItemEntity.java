package com.muniu.cloud.lucifer.share.service.entity;

import com.muniu.cloud.lucifer.commons.core.jpa.entity.BaseAutoIdEntity;
import com.muniu.cloud.lucifer.commons.core.jpa.interfaces.JpaCreateColumn;
import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleDateType;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleParams;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
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
public class ShareRuleItemEntity extends BaseAutoIdEntity implements JpaCreateColumn {

    @Serial
    private static final long serialVersionUID = 1L;

    @Column(name = "create_time", nullable = false, updatable = false, comment = "创建时间")
    private long createTime;

    /**
     * cron表达式
     */
    @Column(name = "cron", length = 255)
    private String cron;

    /**
     * 规则组
     */
    @Column(name = "rule_group", nullable = false)
    private long ruleGroup;

    /**
     * 规则数据源
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "data_source", length = 50, nullable = false)
    private RuleDataSource dataSource;

    /**
     * 规则值
     */
    @Column(name = "rule_value", length = 1000, nullable = false)
    private String ruleValue;

    /**
     * 排序
     */
    @Column(name = "sort", nullable = false)
    private int sort;


    /**
     * 日期类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "date_type", length = 50)
    private RuleDateType dateType;

    /**
     * 日期值
     */
    @Column(name = "date_value", length = 50)
    private String dateValue;







    public ShareRuleItemEntity() {
    }


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
