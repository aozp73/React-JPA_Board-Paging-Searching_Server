package com.example.demo.config.security;

import com.example.demo.config.security.filter.MyJwtAuthenticationFilter;
import com.example.demo.config.security.jwt.MyJwtProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.web.filter.CorsFilter;

@Configuration
@RequiredArgsConstructor
public class MyAuthenticationManagerConfig extends AbstractHttpConfigurer<MyAuthenticationManagerConfig, HttpSecurity> {

    private final MyJwtProvider myJwtProvider;

    @Override
    public void configure(HttpSecurity builder) {
        builder.addFilterBefore(
                        new MyJwtAuthenticationFilter(myJwtProvider),
                        CorsFilter.class);
    }
}
