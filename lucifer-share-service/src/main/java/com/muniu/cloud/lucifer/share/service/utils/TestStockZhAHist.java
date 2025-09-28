package com.muniu.cloud.lucifer.share.service.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.core.config.LuciferLoadStrategy;
import com.muniu.cloud.lucifer.commons.core.http.LuciferAutoProxyHttpClient;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.core.http.LuciferProxy;
import com.muniu.cloud.lucifer.commons.core.http.LuciferStaticProxyHttpClient;
import com.muniu.cloud.lucifer.commons.utils.constants.DateConstant;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;

import java.net.Proxy;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

@Slf4j
public class TestStockZhAHist {


    private static String proxyUrl = "http://proxy.siyetian.com/apis_get2.html?token=hVHavFGduFWLORUT49EVJdXTqV1dPRVS55EVnh3TR1STqFUeORVQ10kaJFjTUFleNR0Y35EVJRjTUdGN.QOxgzNyUDO1cTM&limit=1&type=0&time=&data_format=json&showTimeEnd=true";
    private static String authKey = "antaohua";
    private static String password = "1q2w3e4rQ663463";

    public static void main(String[] args) {
//        LuciferAutoProxyHttpClient autoProxyHttpClient =  new LuciferAutoProxyHttpClient(client -> {
//            String json = new LuciferHttpClient().get(proxyUrl);
//            JSONObject object = JSON.parseObject(json);
//            if (object.getInteger("code") != 1 || object.getJSONArray("data") == null || object.getJSONArray("data").isEmpty()) {
//                log.error("load proxy config error . jsonString = {}", json);
//                return new LuciferProxy(Proxy.Type.HTTP, "127.0.0.1", 80);
//            }
//            JSONArray data = object.getJSONArray("data");
//            if (data.isEmpty()) {
//                log.error("load proxy config error . jsonString = {}", json);
//                return new LuciferProxy(Proxy.Type.HTTP, "127.0.0.1", 80);
//            }
//            JSONObject obj = data.getJSONObject(0);
//            LocalDateTime localDateTime = LocalDateTime.parse(obj.getString("end_time"), DateConstant.DATE_FORMATTER_YYYY_MM_DD_HH_MM_SS);
//            long timestamp = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
//            log.info("load proxy config success . ip = {} , port = {} , timestamp = {} , t={}", obj.getString("ip"), obj.getInteger("port"), timestamp - 10000, timestamp);
//            return new LuciferProxy(Proxy.Type.HTTP, obj.getString("ip"), obj.getInteger("port"), authKey, password, timestamp - 10000);
//        });
        LuciferStaticProxyHttpClient autoProxyHttpClient = new LuciferStaticProxyHttpClient(LuciferLoadStrategy.REQUEST_HASH);
        autoProxyHttpClient.addProxy(Proxy.Type.HTTP, "47.94.175.27", 64764,"antaohua","1q2w3e4rQ663463");
        String symbol = "600000";  // 示例股票代码
        String period = "daily";   // 示例周期
        String adjust = "qfq";     // 示例复权方式
        String startDate = "20250924"; // 示例开始日期
        String endDate = "20250925";   // 示例结束日期

        String json = autoProxyHttpClient.get(buildStockDataUrl(symbol,period,adjust,startDate,endDate));
        System.out.println(json);
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
}

