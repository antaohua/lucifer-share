package com.muniu.cloud.lucifer.share.service.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;

public enum RulesFields {

    HIGH_PRICE("最高价", "high_price", "元", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.AMOUNT, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),
    LOW_PRICE("最低价", "low_price", "元", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.AMOUNT, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),

    OPEN_PRICE("开盘价", "open_price", "元", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.AMOUNT, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),
    CLOSE_PRICE("收盘价", "close_price", "元", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.AMOUNT, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),

    VOLUME("成交量", "volume", "股", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.SHARES, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),
    AMOUNT("成交额", "amount", "元", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.AMOUNT, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),

    TURNOVER_RATE("换手率", "turnover_rate", "%", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.PERCENTAGE, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),
    SHARE_CODE("股票", "share_code", "股票", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.SHARE, RulesOperator.IN, RulesOperator.NOT_IN),
    CHANGE_RATE("涨跌幅", "change_rate", "%", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.PERCENTAGE, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),
    CHANGE_AMOUNT("涨跌额", "change_amount", "元", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.AMOUNT, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),
    AMPLITUDE("振幅", "amplitude", "%", RuleDataSource.HISTORY_SINGLE, RuleItemDataType.PERCENTAGE, RulesOperator.GT, RulesOperator.LT, RulesOperator.GET, RulesOperator.LET, RulesOperator.EQ),


    //    PE("市盈率", "pe", "decimal", ""),
//    PB("市净率", "pb", "decimal", ""),
//    PS("市销率", "ps", "decimal", ""),
//    PCF("市现率", "pcf", "decimal", ""),
//    ADJUSTED_PRICE("复权价", "adjusted_price", "decimal", "元"),
    ;


    private final String name;

    private final String dataColumn;

    private final RuleItemDataType ruleItemDataType;

    private final RuleDataSource ruleDataSource;

    private final RulesOperator[] operator;

    /**
     * 单位
     */
    private final String unit;


    RulesFields(String name, String dataColumn, String unit, RuleDataSource ruleDataSource, RuleItemDataType ruleItemDataType, RulesOperator... operator) {
        this.name = name;
        this.dataColumn = dataColumn;
        this.unit = unit;
        this.ruleItemDataType = ruleItemDataType;
        this.operator = operator;
        this.ruleDataSource = ruleDataSource;
    }

    public RuleDataSource getRuleSource() {
        return ruleDataSource;
    }

    public String getName() {
        return name;
    }

    public String getDataColumn() {
        return dataColumn;
    }

    public RulesOperator[] getOperator() {
        return operator;
    }

    public String getUnit() {
        return unit;
    }

    public RuleItemDataType getRuleItemDataType() {
        return ruleItemDataType;
    }

    public static final List<HistoryFieldBean> HISTORY_FIELDS = Arrays.stream(RulesFields.values()).map(e -> new HistoryFieldBean(e.getName(), e.name(), e.getUnit(), e.getRuleSource(), e.getRuleItemDataType(), RulesOperator.toRulesOperatorList(e.getOperator()))).toList();

    public record HistoryFieldBean(String name, String code, String unit, RuleDataSource ruleDataSource, RuleItemDataType ruleItemDataType,
                                   List<RulesOperator.RulesOperatorBean> operator) {
    }

    public static RulesFields getByName(String name) {
        for (RulesFields rulesField : RulesFields.values()) {
            if(StringUtils.equals(name, rulesField.name())) {
                return rulesField;
            }
        }
        return null;
    }
}
