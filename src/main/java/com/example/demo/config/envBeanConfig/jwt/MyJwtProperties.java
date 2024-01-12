package com.example.demo.config.envBeanConfig.jwt;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties("my.jwt")
public class MyJwtProperties {

    private String accessKey;
    private String refreshKey;

}
