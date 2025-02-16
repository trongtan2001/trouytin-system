package com.roomster.roomsterbackend.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
@Component
@ConfigurationProperties(prefix = "vnpay")
@Data
public class VnpayConfig {
    private String returnUrl;
    private String paymentUrl;
    private String tmnCode;
    private String hashSecret;
    private String version;
}
