package com.muniu.cloud.lucifer.share.service.constant;

import lombok.Getter;

@Getter
public enum AdjustConstant {
    NONE("none", "不复权"){
        @Override
        public String getCode() {
            return "";
        }
    },
    FORWARD("qfq", "前复权"),
    BACKWARD("hfq", "后复权");

    private final String code;
    private final String description;

    AdjustConstant(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public static AdjustConstant getAdjustConstant(String code) {
        for (AdjustConstant adjustConstant : AdjustConstant.values()) {
            if (adjustConstant.getCode().equals(code)) {
                return adjustConstant;
            }
        }
        return NONE;
    }


}
