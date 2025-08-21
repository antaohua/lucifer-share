package com.muniu.cloud.lucifer.share.service.constant;

public enum ShareExchange {
    SZ("SZ", "深圳交易所", "深证"),
    SH("SH", "上海交易所", "上证"),
    BJ("BJ", "北京交易所", "北证"),
    HK("HK", "香港交易所", "港交所"),
    NYSE("NYSE", "纽约证券交易所", "纽约"),
    NASDAQ("NASDAQ", "纳斯达克证券交易所", "纳斯达克"),
    LSE("LSE", "伦敦证券交易所", "伦交所"),
    JPX("JPX", "日本交易所集团", "日交所"),
    SSE("SSE", "新加坡交易所", "新交所"),
    ASX("ASX", "澳大利亚证券交易所", "澳交所"),
    TSE("TSE", "多伦多证券交易所", "多交所"),
    UNKNOWN("unknown", "未知", "未知");
    private final String code;
    private final String name;
    private final String shortName;

    ShareExchange(String code, String name, String shortName) {
        this.code = code;
        this.name = name;
        this.shortName = shortName;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getShortName() {
        return shortName;
    }

    public static ShareExchange getExchangeConstant(String code) {
        for (ShareExchange shareExchange : ShareExchange.values()) {
            if (shareExchange.getCode().equals(code)) {
                return shareExchange;
            }
        }
        return UNKNOWN;
    }
}
