package com.muniu.cloud.lucifer.share.service.constant;

import java.util.HashMap;
import java.util.Map;

public enum RulesConnect {

        AND("AND", "与"),

        OR("OR", "或"),
        ;

        private final String code;

        private final String name;

        RulesConnect(String code, String name) {
            this.code = code;
            this.name = name;
        }

        public String getCode() {
            return code;
        }

        public String getName() {
            return name;
        }

        public static final Map<String, String> RULES_CONNECT_MAP = new HashMap<>();

        static {
            for (RulesConnect type : RulesConnect.values()) {
                RULES_CONNECT_MAP.put(type.getCode(), type.name);
            }
        }

}
