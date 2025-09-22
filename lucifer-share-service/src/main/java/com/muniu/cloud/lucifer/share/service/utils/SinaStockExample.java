package com.muniu.cloud.lucifer.share.service.utils;

import okhttp3.*;

import java.io.IOException;

public class SinaStockExample {

    public static void main1(String[] args) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData" +
                "?page=68&num=80&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Host", "vip.stock.finance.sina.com.cn")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/124.0.0.0 Safari/537.36")
                .addHeader("Accept", "*/*")
                .get()
                .build();



        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String body = response.body().string();
                System.out.println(body);
            } else {
                System.err.println("Request failed: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData" +
                "?page=68&num=80&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page";

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Host", "vip.stock.finance.sina.com.cn")
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
                        "AppleWebKit/537.36 (KHTML, like Gecko) " +
                        "Chrome/124.0.0.0 Safari/537.36")
                .addHeader("Accept", "*/*")
                .get()
                .build();

        // 异步请求
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                System.err.println("Request failed: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful() && response.body() != null) {
                    String body = response.body().string();
                    System.out.println("Response: " + body);
                } else {
                    System.err.println("Request failed: " + response.code());
                }
            }
        });

        // 防止主线程提前退出
        try {
            Thread.sleep(5000); // 等待5秒，确保异步请求有机会完成
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
