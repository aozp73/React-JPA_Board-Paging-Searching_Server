package com.example.demo.config.security;

import com.example.demo.config.security.filter.MyJwtAuthenticationFilter;
import com.example.demo.config.security.jwt.MyJwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

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
    public MyJwtAuthenticationFilter myJwtAuthenticationFilter() throws Exception {
        return new MyJwtAuthenticationFilter(myJwtProvider);
    }
    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {

        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            /**
             *  == header 전송 / refresh token / 응답 DTO 추가 ===== ☆★☆★☆
             */
            log.error("에러 : 인증 실패 : " + authException.getMessage());
            response.sendRedirect("/loginForm");
        });

        http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
            /**
             *  == header 전송 / refresh token / 응답 DTO 추가 ===== ☆★☆★☆
             */
        });

        http.authorizeRequests()
                .antMatchers("/api/auth/**").authenticated()
                .anyRequest().permitAll()

                .and()
                .apply(myAuthenticationManagerConfig)

                .and()
                .formLogin().disable()
                .csrf().disable()
                .cors().configurationSource(configurationSource())

                .and()
                .headers().frameOptions().disable();

        return http.build();
    }

    public CorsConfigurationSource configurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        configuration.addAllowedOriginPattern("*");
        configuration.setAllowCredentials(true);
        configuration.addExposedHeader("Authorization");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}