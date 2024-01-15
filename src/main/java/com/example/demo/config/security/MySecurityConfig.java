package com.example.demo.config.security;

import com.example.demo.config.security.filter.MyJwtAuthenticationFilter;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.util.MySecurityUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.http.HttpServletResponse;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class MySecurityConfig {

    private final MyJwtProvider myJwtProvider;
    private final MyAuthenticationManagerConfig myAuthenticationManagerConfig;

    @Bean
    public BCryptPasswordEncoder encode() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public MyJwtAuthenticationFilter myJwtAuthenticationFilter() {
        return new MyJwtAuthenticationFilter(myJwtProvider);
    }
    
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            MySecurityUtil.handleExceptionResponse(response, "Authentication Fail", HttpServletResponse.SC_BAD_REQUEST);
        });

        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            MySecurityUtil.handleExceptionResponse(response, "Authorization Fail", HttpServletResponse.SC_BAD_REQUEST);
        });

        http.cors().configurationSource(configurationSource())

                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
//                .headers().frameOptions().disable() // h2 개발용도

                .authorizeRequests()
                .requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                .antMatchers("/api/auth/**").authenticated()
                .anyRequest().permitAll()

                .and()
                .apply(myAuthenticationManagerConfig);

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedOrigin("http://localhost:3000");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}