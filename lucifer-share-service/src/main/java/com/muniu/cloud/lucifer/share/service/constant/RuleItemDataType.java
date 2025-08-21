package com.muniu.cloud.lucifer.share.service.constant;

public enum RuleItemDataType {

//    STRING("string", "字符串",RuleStringFunction.class),
//    INTEGER("integer", "整数"),
//    DECIMAL("decimal", "小数"),
//    DATE("date", "日期"),
//    BOOLEAN("boolean", "布尔"),
//    ARRAY("array", "数组", RuleStringFunction.class),
    // 以下是自定义的
    SHARE("share", "股票"),
    PERCENTAGE("percentage", "百分比"),
    AMOUNT("amount", "金额"),
    SHARES("shares", "股数"),
    ;


    private final String code;
    private final String name;



    RuleItemDataType(String code, String name) {
        this.code = code;
        this.name = name;
    }


    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }
}
