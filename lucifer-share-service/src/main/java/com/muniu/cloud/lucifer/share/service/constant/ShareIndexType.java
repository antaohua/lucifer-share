package com.muniu.cloud.lucifer.share.service.constant;

public enum ShareIndexType {

    SH("sh","上证系列指数"),
    SZ("sz","深证系列指数"),
    CSI("csi","中证系列指数")
    ;

    private final String code;
    private final String name;

    ShareIndexType(String code, String name) {
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
