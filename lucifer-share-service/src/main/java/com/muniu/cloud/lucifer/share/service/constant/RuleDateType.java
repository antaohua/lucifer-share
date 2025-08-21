package com.muniu.cloud.lucifer.share.service.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum RuleDateType {

    /**
     * 相对日期 例如：今天、昨天、明天
     */
    RELATIVE("相对日期", RuleDataSource.HISTORY_SINGLE),


    /**
     * 相对日期范围 例如：最近7天、最近30天
     */
    RELATIVE_RANGE("相对日期范围", RuleDataSource.HISTORY_MULTI),


    /**
     * 绝对日期 例如：2021-01-01
     */
    ABSOLUTE("指定日期", RuleDataSource.HISTORY_SINGLE),

    /**
     * 动态日期范围 例如：某日到今天
     */
    DYNAMIC_RANGE("动态日期范围", RuleDataSource.HISTORY_MULTI),

    /**
     * 自定义时间段
     */
    CUSTOM("自定义时间段", RuleDataSource.HISTORY_MULTI),;


    private final String label;

    private final RuleDataSource ruleDataSource;


    RuleDateType(String label, RuleDataSource ruleDataSource) {
        this.label = label;
        this.ruleDataSource = ruleDataSource;
    }

    public String getLabel() {
        return label;
    }

    public RuleDataSource getRuleSource() {
        return ruleDataSource;
    }

    public static final List<RuleDateTypeBean> RULE_DATE_TYPE_LIST = Arrays.stream(RuleDateType.values()).map(RuleDateTypeBean::new).toList();


    public record RuleDateTypeBean(String key, String label, RuleDataSource.RuleSourceBean source) {
        public RuleDateTypeBean(RuleDateType ruleDateType) {
            this(ruleDateType.name(), ruleDateType.getLabel(), new RuleDataSource.RuleSourceBean(ruleDateType.ruleDataSource));
        }
    }

    public static RuleDateType getByName(String name) {
        for (RuleDateType ruleDateType : RuleDateType.values()) {
            if(StringUtils.equals(name, ruleDateType.name())) {
                return ruleDateType;
            }
        }
        return null;
    }


}
