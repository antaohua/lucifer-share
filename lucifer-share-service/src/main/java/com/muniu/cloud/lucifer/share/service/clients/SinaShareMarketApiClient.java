package com.muniu.cloud.lucifer.share.service.clients;

import com.alibaba.fastjson2.JSON;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpCallback;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.share.service.config.ScheduledInterface;
import com.muniu.cloud.lucifer.share.service.model.dto.SinaStockMarketSaveEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SinaShareMarketApiClient implements ScheduledInterface {


    private final ApplicationEventPublisher publisher;

    private final LuciferHttpClient autoProxyHttpClient;

    @Autowired
    public SinaShareMarketApiClient(@Qualifier("autoProxyHttpClient") LuciferHttpClient autoProxyHttpClient, ApplicationEventPublisher publisher) {
        this.publisher = publisher;
        this.autoProxyHttpClient = autoProxyHttpClient;
    }

    @Transactional(rollbackFor = Exception.class)
    public void scheduled() {
        long loadTime = System.currentTimeMillis();
        int pageSize = 100;
        for (int i = 0; i < 55; i++) {
            int page = i + 1;
            autoProxyHttpClient.getAsync("https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=" + page + "&num=" + pageSize + "&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page", new LuciferHttpCallback() {
                @Override
                public void onSuccess(String response) {
                    String jsonString = new String(response.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);
                    List<SinaStockMarketSaveEvent> sinaStockMarketSaveEventList = JSON.parseArray(jsonString, SinaStockMarketSaveEvent.class);
                    if(CollectionUtils.isEmpty(sinaStockMarketSaveEventList)){
                        return;
                    }
                    sinaStockMarketSaveEventList.forEach(sinaStockMarketSaveEvent -> sinaStockMarketSaveEvent.setLoadTime(loadTime));
                    sinaStockMarketSaveEventList.forEach(publisher::publishEvent);
                }

                @Override
                public void onFailure(HttpClientException e) {
                    publisher.publishEvent(e);
                }
            });
        }
    }

    public static void main(String[] args) {
        LuciferHttpClient luciferHttpClient = new LuciferHttpClient();
        String jsonString = luciferHttpClient.get("https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=1&num=100&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page");
        List<SinaStockMarketSaveEvent> sinaStockMarketSaveEventList = JSON.parseArray(jsonString, SinaStockMarketSaveEvent.class);

    }
}
