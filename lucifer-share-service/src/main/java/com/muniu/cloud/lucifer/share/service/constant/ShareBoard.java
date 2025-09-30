package com.muniu.cloud.lucifer.share.service.constant;

import lombok.Getter;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Getter
public enum ShareBoard {
    MAIN("MAIN", "主板", new ShareExchange[]{ShareExchange.SZ, ShareExchange.SH}) {
        @Override
        public BigDecimal maxPrice(BigDecimal price, ShareStatus status) {
            // 最大涨幅 10%，四舍五入保留两位小数
            if (status == ShareStatus.ST) {
                return price.multiply(BigDecimal.valueOf(1.05))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            if (status == ShareStatus.NEW) {
                return BigDecimal.ZERO;
            }
            return price.multiply(BigDecimal.valueOf(1.1))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public BigDecimal minPrice(BigDecimal price, ShareStatus status) {
            // 最大跌幅 10%，四舍五入保留两位小数
            if (status == ShareStatus.ST) {
                return price.multiply(BigDecimal.valueOf(0.95))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            if (status == ShareStatus.NEW) {
                return BigDecimal.ZERO;
            }
            return price.multiply(BigDecimal.valueOf(0.9))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    },
    MAIN_B("MAIN_B", "主板B股", new ShareExchange[]{ShareExchange.SZ, ShareExchange.SH}) {
        @Override
        public BigDecimal maxPrice(BigDecimal price, ShareStatus status) {
            // 最大涨幅 10%，四舍五入保留两位小数
            if (status == ShareStatus.ST) {
                return price.multiply(BigDecimal.valueOf(1.05))
                        .setScale(2, RoundingMode.HALF_UP);
            }
            if (status == ShareStatus.NEW) {
                return BigDecimal.ZERO;
            }
            return price.multiply(BigDecimal.valueOf(1.1))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public BigDecimal minPrice(BigDecimal price, ShareStatus status) {
            // 最大跌幅 10%，四舍五入保留两位小数
            if (status == ShareStatus.ST) {
                return price.multiply(BigDecimal.valueOf(0.95)).setScale(2, RoundingMode.HALF_UP);
            }
            if (status == ShareStatus.NEW) {
                return BigDecimal.ZERO;
            }
            return price.multiply(BigDecimal.valueOf(0.9)).setScale(2, RoundingMode.HALF_UP);
        }
    },
    ChiNext("ChiNext", "创业板", new ShareExchange[]{ShareExchange.SZ}) {
        @Override
        public BigDecimal maxPrice(BigDecimal price, ShareStatus status) {
            // 最大涨幅 20%，四舍五入保留两位小数
            return price.multiply(BigDecimal.valueOf(1.2))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public BigDecimal minPrice(BigDecimal price, ShareStatus status) {
            // 最大跌幅 20%，四舍五入保留两位小数
            return price.multiply(BigDecimal.valueOf(0.8))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    },
    STAR("STAR", "科创板", new ShareExchange[]{ShareExchange.SH}) {
        @Override
        public BigDecimal maxPrice(BigDecimal price, ShareStatus status) {
            return price.multiply(BigDecimal.valueOf(1.2))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public BigDecimal minPrice(BigDecimal price, ShareStatus status) {
            return price.multiply(BigDecimal.valueOf(0.8))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }, // 科创板
    BSE("BSE", "北京证券交易所", new ShareExchange[]{ShareExchange.BJ}) {
        //最大涨幅幅 30% ,小数点后四舍五入保留两位
        @Override
        public BigDecimal maxPrice(BigDecimal price, ShareStatus status) {
            return price.multiply(BigDecimal.valueOf(1.3))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public BigDecimal minPrice(BigDecimal price, ShareStatus status) {
            return price.multiply(BigDecimal.valueOf(0.7))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }, // 北京证券交易所
    CDR("CDR", "存托凭证", new ShareExchange[]{ShareExchange.SZ}) {
        @Override
        public BigDecimal maxPrice(BigDecimal price, ShareStatus status) {
            return price.multiply(BigDecimal.valueOf(1.2))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        @Override
        public BigDecimal minPrice(BigDecimal price, ShareStatus status) {
            return price.multiply(BigDecimal.valueOf(0.8))
                    .setScale(2, RoundingMode.HALF_UP);
        }
    }, // 存托凭证
    UNKNOWN("Unknown", "未知板块", new ShareExchange[]{ShareExchange.UNKNOWN}) {
        @Override
        public BigDecimal maxPrice(BigDecimal price, ShareStatus status) {
            return BigDecimal.ZERO;
        }

        @Override
        public BigDecimal minPrice(BigDecimal price, ShareStatus status) {
            return BigDecimal.ZERO;
        }
    }; // 未知板块

    private final String key;
    private final String dist;
    //交易所
    private final ShareExchange[] exchanges;


    ShareBoard(String key, String dist, ShareExchange[] exchanges) {
        this.key = key;
        this.dist = dist;
        this.exchanges = exchanges;
    }

    public abstract BigDecimal maxPrice(BigDecimal price, ShareStatus status);

    public abstract BigDecimal minPrice(BigDecimal price, ShareStatus status);


    public static ShareBoard fromKey(String key) {
        for (ShareBoard constant : values()) {
            if (constant.key.equals(key)) {
                return constant;
            }
        }
        return UNKNOWN;
    }


    /**
     * 根据股票代码获取板块
     *
     * @param stockCode 股票代码
     * @return 板块类型
     */
    public static ShareBoard getBoard(String stockCode) {
        if (stockCode.startsWith("000") || stockCode.startsWith("001") || stockCode.startsWith("003") || stockCode.startsWith("600") || stockCode.startsWith("601") || stockCode.startsWith("603") || stockCode.startsWith("605")) {
            return MAIN;
        } else if (stockCode.startsWith("002")) {
            return MAIN;
        } else if (stockCode.startsWith("300") || stockCode.startsWith("301")) {
            return ChiNext;
        } else if (stockCode.startsWith("688")) {
            return STAR;
        } else if (stockCode.startsWith("430") || stockCode.startsWith("83") || stockCode.startsWith("87") || stockCode.startsWith("92")) {
            return BSE;
        } else if (stockCode.startsWith("689")) {
            return CDR;
        }
        return UNKNOWN;
    }

}
