package com.muniu.cloud.lucifer.share.service.constant;

import java.util.Arrays;
import java.util.List;

public enum RulesOperator {


    LT( "小于", "<"),
    LET( "小于等于", "<="),
    GT("大于", ">"),
    GET( "大于等于", ">="),
    EQ("等于" ,"="),
    NE("不等于", "!="),
    IN("包含", "in"),
    NOT_IN("不包含", "not in"),
    ;

    private final String description;

    private final String symbol;

    RulesOperator(String description, String symbol) {
        this.description = description;
        this.symbol = symbol;
    }


    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }

    public static RulesOperator getRulesOperator(String description) {
        for (RulesOperator operator : RulesOperator.values()) {
            if (operator.description.equals(description)) {
                return operator;
            }
        }
        return null;
    }

    public static RulesOperator getRulesOperatorBySymbol(String symbol) {
        for (RulesOperator operator : RulesOperator.values()) {
            if (operator.getSymbol().equals(symbol)) {
                return operator;
            }
        }
        return null;
    }


    public static final List<RulesOperatorBean> RULES_OPERATOR_LIST = Arrays.stream(RulesOperator.values()).map(RulesOperatorBean::new).toList();

    public record RulesOperatorBean(String value,String key) {
        public RulesOperatorBean(RulesOperator rulesOperator) {
            this(rulesOperator.getDescription(), rulesOperator.name());
        }
    }

    public static List<RulesOperatorBean> toRulesOperatorList(RulesOperator... rulesOperators) {
        return Arrays.stream(rulesOperators).map(RulesOperatorBean::new).toList();
    }

}
