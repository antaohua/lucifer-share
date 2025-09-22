package com.muniu.cloud.lucifer.share.service.clients;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferProxySelector;
import com.muniu.cloud.lucifer.share.service.model.dto.StockMarketItem;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ShareMarketApiClient {

    private static String apiUrl = "http://proxy.siyetian.com/apis_get2.html?token=hVHavFGduFWLORUT49EVJdXTqV1dPRVS55EVnh3TR1STqFUeORVQ10kaJFjTUFleNR0Y35EVJRjTUdGN.QOxgzNyUDO1cTM&limit=1&type=0&time=&data_format=json&showTimeEnd=true";
    private static String authKey = "antaohua";
    private static String password = "1q2w3e4rQ663463";

    public static void main(String[] args) throws UnsupportedEncodingException {
        ShareMarketApiClient shareMarketApiClient = new ShareMarketApiClient();

        LuciferHttpClient okHttpClient = new LuciferHttpClient(LuciferProxySelector.ROUND_ROBIN);
        String result = okHttpClient.get(apiUrl);
        System.out.println(result);
        JSONObject object = JSON.parseObject(result);
        if (object.getInteger("code") == 1) {
            List<Proxy> data = object.getList("data", Proxy.class);
            for (Proxy proxy : data) {
                System.out.println(proxy.getIp() + " " + proxy.getPort());
                shareMarketApiClient.sinaShareMarketApiClient(proxy);
            }


        }
//        {"code":1,"info":"获取成功","data":[{"ip":"115.230.172.255","port":"22744","end_time":"2025-09-22 15:57:03"}]}


    }


    public void sinaShareMarketApiClient(Proxy proxy) throws UnsupportedEncodingException {
        LuciferHttpClient okHttpClient = new LuciferHttpClient(LuciferProxySelector.ROUND_ROBIN);
        okHttpClient.addProxy(java.net.Proxy.Type.HTTP, proxy.getIp(), proxy.getPort(), authKey, password);
        String data = okHttpClient.get("https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=1&num=100&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page");
        String jsonString = new String(data.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        List<StockMarketItem> stockMarketItemList = JSON.parseArray(jsonString, StockMarketItem.class);
        System.out.println(stockMarketItemList.size());
        System.out.println(jsonString);
        System.out.println(URLDecoder.decode(stockMarketItemList.getFirst().getName(), StandardCharsets.UTF_8));
    }
}
