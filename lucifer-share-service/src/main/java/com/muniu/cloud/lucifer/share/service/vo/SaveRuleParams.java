package com.muniu.cloud.lucifer.share.service.vo;

import com.muniu.cloud.lucifer.commons.model.base.BaseModel;
import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleDateType;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class SaveRuleParams extends BaseModel {

    /**
     * cron表达式
     */
    private String cron;

    /**
     * 规则组
     */
    private Long ruleGroup;

    /**
     * 规则类型
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

    private RuleDateType dateType;

    private String dateValue;


}
