package com.muniu.cloud.lucifer.share.service.constant;

public enum PeriodConstant {

    DAY("daily", "日K"),
    WEEK("weekly", "周K"),
    MONTH("monthly", "月K");

    private final String code;
    private final String description;

    PeriodConstant(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

}
