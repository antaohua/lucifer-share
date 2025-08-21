package com.muniu.cloud.lucifer.share.service.constant;

public enum RuleDataSource {

    /**
     * 历史单条规则
     */
    HISTORY_SINGLE(RulesType.history, "历史单条规则"),

    /**
     * 历史多条规则
     */
    HISTORY_MULTI(RulesType.history, "历史多条规则"),

    /**
     * 分时单条规则
     */
    MARKET_SINGLE(RulesType.market, "分时单条规则"),

    /**
     * 分时多条规则
     */
    MARKET_MULTI(RulesType.market, "分时多条规则"),

    ;
    private final RulesType type;

    private final String description;

    RuleDataSource(RulesType type, String description) {
        this.type = type;
        this.description = description;
    }


    public RulesType getType() {
        return type;
    }

    public String getDescription() {
        return description;
    }

    public record RuleSourceBean(String key, String label,String type){
        public RuleSourceBean(RuleDataSource ruleDataSource) {
            this(ruleDataSource.name(), ruleDataSource.getDescription(), ruleDataSource.getType().name());
        }
    }
}
