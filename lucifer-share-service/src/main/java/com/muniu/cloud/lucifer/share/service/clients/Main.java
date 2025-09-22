
package com.muniu.cloud.lucifer.share.service.clients;
import cn.hutool.http.HttpUtil;
import okhttp3.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Base64;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
public class Main {
    // 需要请求的目标网址
    private static final String TARGET_URL = "https://vip.stock.finance.sina.com.cn/quotes_service/api/json_v2.php/Market_Center.getHQNodeData?page=1&num=100&sort=symbol&asc=1&node=hs_a&symbol=&_s_r_a=page";
    // 提取链接
    private static final String API_URL = "http://proxy.siyetian.com/apis_get2.html?token=hVHavFGduFWLORUT49EVJdXTqV1dPRVS55EVnh3TR1STqFUeORVQ10kaJFjTUFleNR0Y35EVJRjTUdGN.QN0QTOyUDO1cTM&limit=1&type=0&time=&split=1&split_text=";
    public static void main(String[] args) {
        String result = HttpUtil.get(API_URL);
        System.out.println("从接口获取回来:" + result.strip());
        String proxyIp = result.strip();
        try {
            Proxies(TARGET_URL, proxyIp);
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static void Proxies(String urlString, String ipPort) {
        try {
            Response response = null;
            try {
                System.out.println("当前ip：" + ipPort);
                String ip = ipPort.split(":")[0];
                int port = Integer.parseInt(ipPort.split(":")[1]);
                Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress(ip, port));
                OkHttpClient httpClient = new OkHttpClient().newBuilder().proxy(proxy).proxyAuthenticator((route, response1) -> response1.request().newBuilder()
                        .header("Proxy-Authorization", Credentials.basic("antaohua", "1q2w3e4rQ663463"))
                        .build()).connectTimeout(10, TimeUnit.SECONDS)
                        .build();
                Request request = null;
                request = (new Request.Builder().url(urlString).build());
                response = httpClient.newCall(request).execute();
                System.out.println("输出结果2：" + Objects.requireNonNull(response.body()).string());
            } catch (IOException e) {
                System.out.println("错误提示1");
                e.printStackTrace();
            }
        } catch (Exception e) { System.out.println("错误提示2");
            e.printStackTrace();
        }
    }

    public static void main1(String[] args) {
        String proxyAuth = "antaohua:1q2w3e4rQ663463";
        String encodedProxyAuth = Base64.getEncoder().encodeToString(proxyAuth.getBytes());
        System.out.println(encodedProxyAuth);
        String  s = Credentials.basic("antaohua", "1q2w3e4rQ663463");
        System.out.println(s);

    }
}

//或者
//测试2:
//        import java.net.HttpURLConnection;
//import java.net.MalformedURLException;
//import java.net.Proxy;
//import java.net.URL;
//import java.util.Base64;
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStreamReader;
//
//
//public class ProxyExample {
//
//&nbsp; &nbsp; public static void main(String[] args) throws IOException {
//&nbsp; &nbsp; &nbsp; &nbsp; String url = "https://ip.tool.lu";
//&nbsp; &nbsp; &nbsp; &nbsp; String proxyIp = "代理ip";
//&nbsp; &nbsp; &nbsp; &nbsp; int proxyPort = 端口;
//
//&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; // 这里是实际的代理账号和密码使用&nbsp;&nbsp;
//
//&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; // String proxyAuth = “user:pass”;
//
//
//        //String encodedProxyAuth = Base64.getEncoder().encodeToString(proxyAuth.getBytes());
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Set proxy properties
//&nbsp; &nbsp; &nbsp; &nbsp; System.setProperty("http.proxySet", "true");
//&nbsp; &nbsp; &nbsp; &nbsp; System.setProperty("http.proxyHost", proxyIp);
//&nbsp; &nbsp; &nbsp; &nbsp; System.setProperty("http.proxyPort", String.valueOf(proxyPort));
//&nbsp; &nbsp; &nbsp; &nbsp; System.setProperty("https.proxyHost", proxyIp);
//&nbsp; &nbsp; &nbsp; &nbsp; System.setProperty("https.proxyPort", String.valueOf(proxyPort));
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Create URL object
//&nbsp; &nbsp; &nbsp; &nbsp; URL urlObj = new URL(url);
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Open connection
//&nbsp; &nbsp; &nbsp; &nbsp; HttpURLConnection conn = (HttpURLConnection) urlObj.openConnection(Proxy.NO_PROXY);
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Set headers
//&nbsp; &nbsp; &nbsp; &nbsp; conn.setRequestProperty("User-Agent", "Mozilla/5.0");
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Set proxy authorization header 这里是实际的代理账号和密码使用
//&nbsp; &nbsp; &nbsp; &nbsp; // conn.setRequestProperty("Proxy-Authorization", "Basic " + encodedProxyAuth);
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Send GET request
//&nbsp; &nbsp; &nbsp; &nbsp; conn.setRequestMethod("GET");
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Read response
//&nbsp; &nbsp; &nbsp; &nbsp; BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
//&nbsp; &nbsp; &nbsp; &nbsp; String inputLine;
//&nbsp; &nbsp; &nbsp; &nbsp; StringBuffer response = new StringBuffer();
//&nbsp; &nbsp; &nbsp; &nbsp; while ((inputLine = in.readLine()) != null) {
//&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; response.append(inputLine);
//&nbsp; &nbsp; &nbsp; &nbsp; }
//&nbsp; &nbsp; &nbsp; &nbsp; in.close();
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Print response
//&nbsp; &nbsp; &nbsp; &nbsp; System.out.println(response.toString());
//
//&nbsp; &nbsp; &nbsp; &nbsp; // Get response time
//&nbsp; &nbsp; &nbsp; &nbsp; long responseTime = conn.getHeaderFieldDate("Date", 0) - conn.getHeaderFieldDate("Date", 0);
//&nbsp; &nbsp; &nbsp; &nbsp; System.out.println(responseTime);
//&nbsp; &nbsp; }
//}