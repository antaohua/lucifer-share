package com.muniu.cloud.lucifer.share.service.clients;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.share.service.constant.AdjustConstant;
import com.muniu.cloud.lucifer.share.service.constant.PeriodConstant;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class EastmoneyStockHistApiClient {

    private final LuciferHttpClient autoProxyHttpClient;
    private final LuciferHttpClient staticProxyHttpClient;


    public EastmoneyStockHistApiClient(@Qualifier("autoProxyHttpClient") LuciferHttpClient autoProxyHttpClient) {
        this.autoProxyHttpClient = autoProxyHttpClient;
        this.staticProxyHttpClient = autoProxyHttpClient;
    }


    public String stockZhAHist(String shareCode, String startDate, String endDate) {

        String url = buildStockDataUrl(shareCode, PeriodConstant.DAY.getCode(), AdjustConstant.NONE.getCode(), startDate, endDate);
        String body = null;
        try {
            body= staticProxyHttpClient.get(url);
        }catch (HttpClientException e){
            log.error("error :staticProxyHttpClient shareCode:{}, startDate:{}, endDate={}, error:{}", shareCode, startDate, endDate, e.getMessage());
            body = autoProxyHttpClient.get(url);
        }
        JSONObject root = JSON.parseObject(body);
        JSONObject dataNode = root.getJSONObject("data");
        if (dataNode == null) {
            return null;
        }

        JSONArray klinesNode = dataNode.getJSONArray("klines");
        if (klinesNode == null) {
            return null;
        }

        List<KlineRecord> records = new ArrayList<>();
        for (int i = 0; i < klinesNode.size(); i++) {
            String[] parts = klinesNode.getString(i).split(",");
            KlineRecord record = new KlineRecord();
            record.日期 = parts[0];
            record.开盘 = Double.parseDouble(parts[1]);
            record.收盘 = Double.parseDouble(parts[2]);
            record.最高 = Double.parseDouble(parts[3]);
            record.最低 = Double.parseDouble(parts[4]);
            record.成交量 = Long.parseLong(parts[5]);
            record.成交额 = Double.parseDouble(parts[6]);
            record.振幅 = Double.parseDouble(parts[7]);
            record.涨跌幅 = Double.parseDouble(parts[8]);
            record.涨跌额 = Double.parseDouble(parts[9]);
            record.换手率 = Double.parseDouble(parts[10]);
            record.股票代码 = dataNode.getString("code");
            record.name = dataNode.getString("name");
            records.add(record);
        }
        return JSON.toJSONString(records);

    }


    @Getter
    static class KlineRecord {
        String 日期;
        String 股票代码;
        double 开盘;
        double 收盘;
        double 最高;
        double 最低;
        long 成交量;
        double 成交额;
        double 振幅;
        double 涨跌幅;
        double 涨跌额;
        double 换手率;
        String name;
    }



    public static String buildStockDataUrl(String symbol, String period, String adjust, String startDate, String endDate) {
        // 判断 market_code
        String marketCode = symbol.startsWith("6") ? "1" : "0";

        // 调整和周期字典
        Map<String, String> adjustDict = Map.of(
                "qfq", "1",
                "hfq", "2",
                "", "0"
        );
        Map<String, String> periodDict = Map.of(
                "daily", "101",
                "weekly", "102",
                "monthly", "103"
        );

        // 构建 URL 和请求参数
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://push2his.eastmoney.com/api/qt/stock/kline/get").newBuilder();
        urlBuilder.addQueryParameter("fields1", "f1,f2,f3,f4,f5,f6");
        urlBuilder.addQueryParameter("fields2", "f51,f52,f53,f54,f55,f56,f57,f58,f59,f60,f61,f116");
        urlBuilder.addQueryParameter("ut", "7eea3edcaed734bea9cbfc24409ed989");
        urlBuilder.addQueryParameter("klt", periodDict.get(period));
        urlBuilder.addQueryParameter("fqt", adjustDict.get(adjust));
        urlBuilder.addQueryParameter("secid", marketCode + "." + symbol);
        urlBuilder.addQueryParameter("beg", startDate);
        urlBuilder.addQueryParameter("end", endDate);

        // 返回构建的 URL 字符串
        return urlBuilder.build().toString();
    }


    public static void main(String[] args) {
        String body = "{\"rc\":0,\"rt\":17,\"svr\":181669733,\"lt\":1,\"full\":0,\"dlmkts\":\"\",\"data\":{\"code\":\"600000\",\"market\":1,\"name\":\"浦发银行\",\"decimal\":2,\"dktotal\":6159,\"preKPrice\":12.45,\"klines\":[\"2025-09-24,12.45,12.22,12.54,12.06,1027559,1255684176.00,3.86,-1.85,-0.23,0.34\",\"2025-09-25,12.23,12.43,12.55,12.13,1288851,1599981440.00,3.44,1.72,0.21,0.42\"]}}\n";
        JSONObject root = JSON.parseObject(body);
        JSONObject dataNode = root.getJSONObject("data");


        JSONArray klinesNode = dataNode.getJSONArray("klines");


        List<KlineRecord> records = new ArrayList<>();
        for (int i = 0; i < klinesNode.size(); i++) {
            String[] parts = klinesNode.getString(i).split(",");
            KlineRecord record = new KlineRecord();
            record.日期 = parts[0];
            record.开盘 = Double.parseDouble(parts[1]);
            record.收盘 = Double.parseDouble(parts[2]);
            record.最高 = Double.parseDouble(parts[3]);
            record.最低 = Double.parseDouble(parts[4]);
            record.成交量 = Long.parseLong(parts[5]);
            record.成交额 = Double.parseDouble(parts[6]);
            record.振幅 = Double.parseDouble(parts[7]);
            record.涨跌幅 = Double.parseDouble(parts[8]);
            record.涨跌额 = Double.parseDouble(parts[9]);
            record.换手率 = Double.parseDouble(parts[10]);
            record.股票代码 = dataNode.getString("code");
            record.name = dataNode.getString("name");
            records.add(record);
        }
        System.out.println(JSON.toJSONString(records));
    }


}
