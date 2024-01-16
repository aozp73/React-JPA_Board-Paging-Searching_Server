package com.example.demo.module.refreshtoken;

import com.example.demo.exception.statuscode.Exception400;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor @Slf4j
public class RefreshTokenService {

    private final StringRedisTemplate stringRedisTemplate;

    @Transactional(readOnly = true)
    public String findRefreshToken(String refreshToken) {
        log.debug(("accessToken 재발급 - POST, Service (findRefreshToken)"));

        return Optional.ofNullable(stringRedisTemplate.opsForValue().get("refreshTokenIndex:" + refreshToken))
                .orElseThrow(() -> new Exception400("잘못된 RefreshToken 전송"));
    }
}
