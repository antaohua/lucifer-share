package com.muniu.cloud.lucifer.share.service.clients;

import com.alibaba.fastjson2.JSON;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferProxySelector;
import com.muniu.cloud.lucifer.share.service.model.dto.StockMarketItem;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class ShareMarketApiClient {

    public static void main(String[] args) throws UnsupportedEncodingException {
        ShareMarketApiClient shareMarketApiClient = new ShareMarketApiClient();
        shareMarketApiClient.sinaShareMarketApiClient();
    }


    public void sinaShareMarketApiClient() throws UnsupportedEncodingException {
        LuciferHttpClient okHttpClient = new LuciferHttpClient(LuciferProxySelector.ROUND_ROBIN);
        okHttpClient.addProxy(Proxy.Type.HTTP, "8.219.211.203",8089);
        String data = okHttpClient.get("http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=1&num=100&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page");
        String jsonString = new String(data.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
        List<StockMarketItem> stockMarketItemList = JSON.parseArray(jsonString, StockMarketItem.class);
        System.out.println(stockMarketItemList.size());
        System.out.println(jsonString);
        System.out.println(URLDecoder.decode(stockMarketItemList.getFirst().getName(),StandardCharsets.UTF_8));
    }
}
