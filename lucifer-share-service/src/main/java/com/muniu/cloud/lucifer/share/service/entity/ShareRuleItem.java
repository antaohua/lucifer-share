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

/**
 * <p>
 * 
 * </p>
 *
 * @author antaohua
 * @since 2024-12-26
 */
@Data
@ToString
@TableName("share_rule_item")
public class ShareRuleItem implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 创建时间
     */
    private Long createTime;

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 规则组
     */
    private Long ruleGroup;

    /**
     * 规则数据源
     */
    private RuleDataSource dataSource;

    /**
     * 规则值
     */
    private String ruleValue;

    /**
     * 排序
     */
    private int sort;


    /**
     * 日期类型
     */
    private RuleDateType dateType;

    /**
     * 日期值
     */
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
