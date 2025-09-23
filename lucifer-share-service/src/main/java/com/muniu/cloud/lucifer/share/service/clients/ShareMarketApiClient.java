package com.muniu.cloud.lucifer.share.service.clients;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferHttpCallback;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferProxySelector;
import com.muniu.cloud.lucifer.share.service.model.dto.StockMarketItem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class ShareMarketApiClient {




    private final LuciferHttpClient luciferHttpClient ;

    @Value("${proxy.auth-key}")
    private String authKey;

    @Value("${proxy.password}")
    private String password;

    @Autowired
    public ShareMarketApiClient(@Qualifier("proxyHttpClient") LuciferHttpClient luciferHttpClient) {
        this.luciferHttpClient = luciferHttpClient;
    }




//    public static void main(String[] args) throws UnsupportedEncodingException {
//        ShareMarketApiClient shareMarketApiClient = new ShareMarketApiClient();
//
//        LuciferHttpClient okHttpClient = new LuciferHttpClient(LuciferProxySelector.ROUND_ROBIN);
//        String result = okHttpClient.get(apiUrl);
//        System.out.println(result);
//        JSONObject object = JSON.parseObject(result);
//        if (object.getInteger("code") == 1) {
//            List<Proxy> data = object.getList("data", Proxy.class);
//            for (Proxy proxy : data) {
//                System.out.println(proxy.getIp() + " " + proxy.getPort());
//                shareMarketApiClient.sinaShareMarketApiClient(proxy);
//            }
//
//
//        }
////        {"code":1,"info":"获取成功","data":[{"ip":"115.230.172.255","port":"22744","end_time":"2025-09-22 15:57:03"}]}
//
//
//    }
//    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
//
//    public void sinaShareMarketApiClient(Proxy proxy) throws UnsupportedEncodingException {
//        LuciferHttpClient okHttpClient = new LuciferHttpClient(LuciferProxySelector.ROUND_ROBIN);
//
//        LocalDateTime localDateTime = LocalDateTime.parse(proxy.getEndTime(), formatter);
//        // 转成时间戳（毫秒）
//        long timestamp = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
//        okHttpClient.addProxy(java.net.Proxy.Type.HTTP, proxy.getIp(), proxy.getPort(), authKey, password, timestamp);
//        System.out.println(timestamp - System.currentTimeMillis());
//
//
//        List<StockMarketItem> list = new ArrayList<>();
//        int pageSize = 100;
//        for(int i = 0; i < 55; i++){
//            int page = i + 1;
//            okHttpClient.getAsync("https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=" + page + "&num=" + pageSize + "&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page", new LuciferHttpCallback(){
//                @Override
//                public void onSuccess(String response) {
//                    String jsonString = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
//                    List<StockMarketItem> stockMarketItemList = JSON.parseArray(jsonString, StockMarketItem.class);
//                    list.addAll(stockMarketItemList);
//                    System.out.println(stockMarketItemList.size());
//////                    System.out.println(jsonString);
////                    System.out.println(URLDecoder.decode(stockMarketItemList.getFirst().getName(), StandardCharsets.UTF_8));
//                }
//                @Override
//                public void onFailure(HttpClientException e) {
//
//                }
//            });
//        }
//        System.out.println(list.size());
//    }
}
