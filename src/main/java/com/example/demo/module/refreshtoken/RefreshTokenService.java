package com.example.demo.module.refreshtoken;

import com.example.demo.exception.statuscode.Exception400;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor @Slf4j
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;

    @Transactional(readOnly = true)
    public RefreshToken findRefreshToken(String refreshToken) {
        log.debug(("accessToken 재발급 - POST, Service (findRefreshToken)"));

        return refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new Exception400("Server에 RefreshToken이 존재하지 않습니다. (재로그인 필요)"));
    }
}
