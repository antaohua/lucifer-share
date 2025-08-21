package com.muniu.cloud.lucifer.share.service.constant;

import java.util.HashMap;
import java.util.Map;

public enum RulesType {

    history("history", "历史"),

    market("market", "分时"),


    ;

    private final String code;

    private final String name;

    RulesType(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public static RulesType getRulesType(String code) {
        for (RulesType type : RulesType.values()) {
            if (type.getCode().equals(code)) {
                return type;
            }
        }
        return null;
    }


    public static final Map<String, String> RULES_TYPE_MAP = toMap();

    private static Map<String, String> toMap() {
        Map<String, String> map = new HashMap<>();
        for (RulesType type : RulesType.values()) {
            map.put(type.getCode(), type.getName());
        }
        return map;
    }

}
