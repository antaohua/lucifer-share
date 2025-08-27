package com.muniu.cloud.lucifer.share.service.vo;

import com.muniu.cloud.lucifer.commons.model.base.BaseModel;
import com.muniu.cloud.lucifer.share.service.constant.RuleDataSource;
import com.muniu.cloud.lucifer.share.service.constant.RuleDateType;

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


    public String getCron() {
        return cron;
    }

    public void setCron(String cron) {
        this.cron = cron;
    }

    public Long getRuleGroup() {
        return ruleGroup;
    }

    public void setRuleGroup(Long ruleGroup) {
        this.ruleGroup = ruleGroup;
    }

    public RuleDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(RuleDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public String getRuleValue() {
        return ruleValue;
    }

    public void setRuleValue(String ruleValue) {
        this.ruleValue = ruleValue;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public RuleDateType getDateType() {
        return dateType;
    }

    public void setDateType(RuleDateType dateType) {
        this.dateType = dateType;
    }

    public String getDateValue() {
        return dateValue;
    }

    public void setDateValue(String dateValue) {
        this.dateValue = dateValue;
    }
}
