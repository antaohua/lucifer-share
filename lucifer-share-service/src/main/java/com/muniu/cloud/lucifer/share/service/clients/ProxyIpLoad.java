package com.muniu.cloud.lucifer.share.service.clients;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.muniu.cloud.lucifer.commons.core.delay.DelayQueue;
import com.muniu.cloud.lucifer.commons.core.delay.DelayQueueFactory;
import com.muniu.cloud.lucifer.commons.utils.exception.HttpClientException;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferHttpCallback;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferHttpClient;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferProxy;
import com.muniu.cloud.lucifer.commons.utils.http.LuciferProxySelector;
import com.muniu.cloud.lucifer.share.service.model.dto.StockMarketItem;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class ProxyIpLoad {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private final LuciferHttpClient proxyHttpClient ;

    private final LuciferHttpClient httpClient;

    @Value("${proxy.auth-key}")
    private String authKey;

    @Value("${proxy.password}")
    private String password;

    @Value("${proxy.url}")
    private String proxyUrl;

    //失败次数
    private final AtomicInteger failCount = new AtomicInteger(0);

    @Autowired
    public ProxyIpLoad(@Qualifier("proxyHttpClient") LuciferHttpClient proxyHttpClient, @Qualifier("httpClient") LuciferHttpClient httpClient) {
        this.proxyHttpClient = proxyHttpClient;
        this.httpClient = httpClient;
    }

    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    private final DelayQueue<LuciferProxy>  delayQueue = DelayQueueFactory.createDelayQueue("proxyIpLoad", LuciferProxy.class, (topic,message)-> executorService.execute(this::loadProxyIp));


    private void loadProxyIp() {
        try {
            String json = httpClient.get(proxyUrl);
            JSONObject object = JSON.parseObject(json);
            if (object.getInteger("code") != 1 || object.getJSONArray("data") == null || object.getJSONArray("data").isEmpty()) {
                throw new HttpClientException(500, "获取代理IP失败");
            }
            failCount.set(0);
            JSONArray data = object.getJSONArray("data");
            for (int i = 0; i < data.size(); i++) {
                JSONObject obj = data.getJSONObject(i);
                LocalDateTime localDateTime = LocalDateTime.parse(obj.getString("end_time"), FORMATTER);
                long timestamp = localDateTime.atZone(ZoneId.of("Asia/Shanghai")).toInstant().toEpochMilli();
                LuciferProxy proxy = new LuciferProxy(Proxy.Type.HTTP, obj.getString("ip"), obj.getInteger("port"), authKey, password, timestamp);
                proxyHttpClient.addProxy(proxy);
                delayQueue.offer(proxy.getEndTime() - System.currentTimeMillis() - 10000, TimeUnit.MILLISECONDS, proxy);
            }
        }catch (Exception e){
            log.error("获取代理IP失败");
            if(failCount.get() < 5){
                failCount.incrementAndGet();
            }else {
                delayQueue.offer(3000, TimeUnit.MILLISECONDS, null);
            }
        }
    }


    @PostConstruct
    public void start() {
        executorService.execute(this::loadProxyIp);
    }


}
