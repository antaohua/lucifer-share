package com.muniu.cloud.lucifer.share.service.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleDateType;
import com.muniu.cloud.lucifer.share.service.vo.SaveRuleParams;
import lombok.Data;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * <p>
 * 
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
@Entity
@Table(name = "share_rule_item")
@Data
@ToString
@TableName("share_rule_item")
public class ShareRuleItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    @Column(name = "create_time", nullable = false)
    private Long createTime;

    /**
     * cron表达式
     */
    @Column(name = "cron", length = 255)
    private String cron;

    /**
     * 规则组
     */
    @Column(name = "rule_group", nullable = false)
    private Long ruleGroup;

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







    public ShareRuleItem() {
    }


    public ShareRuleItem(Long createTime, Long groupId, SaveRuleParams saveRuleParams) {
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
