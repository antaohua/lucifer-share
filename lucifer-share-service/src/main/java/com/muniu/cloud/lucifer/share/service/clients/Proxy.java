package com.muniu.cloud.lucifer.share.service.clients;

import com.alibaba.fastjson2.annotation.JSONField;
import lombok.Data;

@Data
public class Proxy {

    private String ip;

    private int port;

    @JSONField(name = "end_time")
    private String endTime;
}
