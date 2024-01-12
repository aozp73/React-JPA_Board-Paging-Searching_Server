package com.example.demo.config.envBeanConfig.jwt;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MyJwtSource {

    private String accessKey;
    private String refreshKey;

}
