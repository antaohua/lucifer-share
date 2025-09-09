package com.muniu.cloud.lucifer.share.service.clients;

import com.muniu.cloud.lucifer.commons.utils.http.OkHttpClient3Util;
import okhttp3.OkHttpClient;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;

public class ShareMarketApiClient {

    public static void main(String[] args) throws UnsupportedEncodingException {
        ShareMarketApiClient shareMarketApiClient = new ShareMarketApiClient();
        shareMarketApiClient.sinaShareMarketApiClient();
    }


    public void sinaShareMarketApiClient() throws UnsupportedEncodingException {
        String data = OkHttpClient3Util.
                get("http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=68&num=80&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page");

        System.out.println(new String(data.getBytes("gbk"), StandardCharsets.UTF_8));

    }
}
