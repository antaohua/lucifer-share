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

    /**
     * 根据股票代码获取交易所
     *
     * @param code 股票代码
     * @return 交易所
     */
    public static ShareExchange getExchange(String code) {
        // 1. 输入校验 (保持不变)
        if (code == null || code.length() != 6 || !code.matches("^\\d{6}$")) {
            // 要求代码为6位数字
            return UNKNOWN;
        }

        String prefix2 = code.substring(0, 2);
        String prefix3 = code.substring(0, 3);

        // --- 上海交易所 (SH) ---
        // 主板 (600, 601, 603, 605), 科创板 (688), B股 (900)
        if (prefix3.startsWith("60") || "688".equals(prefix3) || "900".equals(prefix3)) {
            return SH;
        }
        // --- 深圳交易所 (SZ) ---
        // 主板 (000, 001), 中小板(合并后, 002, 003, 004等), 创业板 (300), B股 (200)
        if (prefix3.startsWith("00") || "300".equals(prefix3) || "200".equals(prefix3)) {
            return SZ;
        }

        // --- 北京交易所 (BJ) ---
        // 北交所股票代码以 43, 83, 87, 88 开头
        if ("43".equals(prefix2) || "83".equals(prefix2) || "87".equals(prefix2) || "88".equals(prefix2)) {
            return BJ;
        }

        // 对于无法识别的代码，返回未知
        return UNKNOWN;
    }

}
