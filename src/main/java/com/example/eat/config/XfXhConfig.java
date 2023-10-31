package com.example.eat.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "xfxh")
@Data
public class XfXhConfig {
    private String hostUrl;
    private String domain;
    private Float temperature;
    private Integer maxTokens;
    private Integer maxResponseTime;
    private String appId;
    private String apiKey;
    private String apiSecret;

}
