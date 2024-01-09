package com.example.demo.config.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Slf4j
@Configuration
public class MySecurityConfig {

    @Bean
    public BCryptPasswordEncoder encode() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.headers().frameOptions().disable(); // h2-console 개발
        http.cors().configurationSource(configurationSource());

        http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
            /**
             *  == header 전송 / refresh token / 응답 DTO 추가 ===== ☆★☆★☆
             */
            log.error("에러 : 인증 실패 : " + authException.getMessage());
            response.sendRedirect("/loginForm");
        });

        http.authorizeRequests()
                .antMatchers("/auth/**").authenticated()
                .anyRequest().permitAll()

                .and()
                .formLogin()
                .loginPage("/loginForm")
                .loginProcessingUrl("/login")
                .defaultSuccessUrl("/")

                .and()
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/loginForm")
                .invalidateHttpSession(true);

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