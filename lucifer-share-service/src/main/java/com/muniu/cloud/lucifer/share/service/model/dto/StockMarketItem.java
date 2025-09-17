package com.muniu.cloud.lucifer.share.service.model.dto;

import lombok.Data;

@Data
public class StockMarketItem {
    private String symbol;
    private String code;
    private String name;
    private String trade;
    private double pricechange;
    private double changepercent;
    private String buy;
    private String sell;
    private String settlement;
    private String open;
    private String high;
    private String low;
    private long volume;
    private long amount;
    private String ticktime;
    private double per;
    private double pb;
    private double mktcap;
    private double nmc;
    private double turnoverratio;
}
