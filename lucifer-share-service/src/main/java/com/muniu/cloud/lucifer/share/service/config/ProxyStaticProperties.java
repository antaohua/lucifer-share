package com.muniu.cloud.lucifer.share.service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author antaohua
 */
@Data
@Component
@ConfigurationProperties(prefix = "proxy.static")
public class ProxyStaticProperties {

    private List<ProxyConfig> config;

    @Data
    public static class ProxyConfig {
        private String host;
        private int port;
        private String username;
        private String password;
        private String type;
    }
}
