package com.muniu.cloud.lucifer.share.service.constant;

import cn.hutool.core.util.StrUtil;
import lombok.Getter;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * @author antaohua
 */

public enum ShareStatus {

    ST(5, "ST", "ST", "*ST"),
    DEMISTED(3, "D", "D"),
    NOT_LISTED(0, "NL", "NL"),
    NEW(1, "N", "N"),
    NORMAL(100, "NM", "NORMAL");


    public static ShareStatus fromCode(String code) {
        for (ShareStatus value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return NORMAL;
    }


    private final int sort;
    @Getter
    private final String[] stockTags;

    @Getter
    private final String code;


    ShareStatus(int sort, String code ,String... stockTags) {
        this.stockTags = stockTags;
        this.code = code;
        this.sort = sort;
    }

    public boolean isStatus(String name){
        return StrUtil.startWithAny(name, getStockTags());
    };


    public static ShareStatus getStatus(String name) {
        List<ShareStatus> shareStatusList = Arrays.stream(values()).sorted(Comparator.comparingInt(n -> n.sort)).toList();
        for (ShareStatus value : shareStatusList) {
            if (value.isStatus(name)) {
                return value;
            }
        }
        return NORMAL;
    }
}
