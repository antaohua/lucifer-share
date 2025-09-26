package com.muniu.cloud.lucifer.share.service.clients;

import com.alibaba.fastjson2.JSON;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpCallback;
import com.muniu.cloud.lucifer.commons.core.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.share.service.impl.TradingDayService;
import com.muniu.cloud.lucifer.share.service.model.dto.SinaStockMarketSaveEvent;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class SinaShareMarketApiClient {


    private final ApplicationEventPublisher publisher;

    private final LuciferHttpClient autoProxyHttpClient;

    private final TradingDayService tradingDayService;




    @Autowired
    public SinaShareMarketApiClient(@Qualifier("autoProxyHttpClient") LuciferHttpClient autoProxyHttpClient, ApplicationEventPublisher publisher, TradingDayService tradingDayService) {
        this.publisher = publisher;
        this.tradingDayService = tradingDayService;
        this.autoProxyHttpClient = autoProxyHttpClient;
    }

    @Scheduled(cron = "0 * * * * MON-FRI")
    @Transactional(rollbackFor = Exception.class)
    public void sinaShareMarketApiClient() throws UnsupportedEncodingException {
        if(!tradingDayService.isTradingTime(true)){
            return;
        }
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
}
