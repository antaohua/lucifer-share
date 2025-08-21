package com.muniu.cloud.lucifer.share.service.constant;

import com.muniu.cloud.lucifer.share.service.impl.TradingDayService;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static com.muniu.cloud.lucifer.commons.utils.constants.DateConstant.DATE_FORMATTER_YYYYMMDD;

public enum ShareStatus {


    STAR_ST(4, "*ST", "*ST") {
        @Override
        public boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
            return StringUtils.indexOf(name, "*ST") > -1;
        }
    },
    //ST
    ST(5, "ST", "ST") {
        @Override
        public boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
            return StringUtils.indexOf(name, "ST") > -1;
        }
    },
    //退市
    DEMISTED(3, "D", "退市") {
        @Override
        public boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
            return false;
        }
    },
    //未上市
    NOT_LISTED(0, "NL", "未上市") {
        @Override
        public boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
            return LocalDate.now().isBefore(LocalDate.of(listDate / 10000, listDate % 10000 / 100, listDate % 100));
        }
    },

    NEW(1, "N", "新股") {
        @Override
        public boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
            int now = Integer.parseInt(LocalDate.now().format(DATE_FORMATTER_YYYYMMDD));
            //北交所首日
            if (board == ShareBoard.BSE) {
                if (tradingDayService.isTradingDay(now)) {
                    return now == listDate;
                }
                return tradingDayService.getPreviousTradingDay(now) == listDate;
            }
            if (now == listDate) {
                return true;
            }
            return tradingDayService.getTradingDaysBetween(Integer.min(now, listDate), Integer.max(now, listDate)).size() < 6;
        }
    },
    //次新股
    NEW_STOCK(2, "NS", "次新股") {
        @Override
        public boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
            //上市日期小于1年
            return LocalDate.now().minusYears(1).isBefore(LocalDate.of(listDate / 10000, listDate % 10000 / 100, listDate % 100));

        }
    },
    //正常
    NORMAL(100, "NORMAL", "正常") {
        @Override
        public boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
            return true;
        }
    };


    public static ShareStatus fromCode(String code) {
        for (ShareStatus value : values()) {
            if (value.code.equals(code)) {
                return value;
            }
        }
        return NORMAL;
    }


    private final int sort;
    private final String code;

    private final String description;


    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    ShareStatus(int sort, String code, String description) {
        this.code = code;
        this.description = description;
        this.sort = sort;
    }

    public abstract boolean isStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService);


    public static ShareStatus getStatus(String name, int listDate, ShareBoard board, TradingDayService tradingDayService) {
        List<ShareStatus> shareStatusList = Arrays.stream(values()).sorted(Comparator.comparingInt(n -> n.sort)).toList();
        for (ShareStatus value : shareStatusList) {
            if (value.isStatus(name, listDate, board, tradingDayService)) {
                return value;
            }
        }
        return NORMAL;
    }

}
