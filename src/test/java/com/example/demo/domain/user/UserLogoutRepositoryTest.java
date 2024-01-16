package com.example.demo.domain.user;

import com.example.demo.module.refreshtoken.RefreshToken;
import com.example.demo.module.refreshtoken.RefreshTokenRepository;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import redis.embedded.RedisServer;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataRedisTest
public class UserLogoutRepositoryTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;
    private static RedisServer redisServer;

    @BeforeEach
    public void init() {
        // rollBack_EmbeddedRedis
        refreshTokenRepository.deleteAll();

        /**
         * [초기 데이터 및 Save]
         * - RefreshToken Entity 1건
         */
        setUp_refreshToken(1L, "mockToken");
    }

    @BeforeAll
    public static void startRedis() {
        redisServer = new RedisServer(6380);
        redisServer.start();
    }

    @AfterAll
    public static void stopRedis() {
        redisServer.stop();
    }

    @Test
    @DisplayName("findByRefreshToken 성공")
    void findByRefreshToken_SuccessTest() {
        // given
        String refreshToken = "mockToken";

        // when
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);

        // then
        assertTrue(refreshTokenEntity.isPresent());

        refreshTokenEntity.ifPresent(foundRefreshToken -> {
            assertEquals(foundRefreshToken.getUserId(), 1L);
            assertEquals(foundRefreshToken.getRefreshToken(), "mockToken");
        });
    }

    @Test
    @DisplayName("findByRefreshToken 실패 - Redis에 없는 AccessToken")
    void findByRefreshToken_WrongToken_FailTest() {
        // given
        String refreshToken = "wrongToken";

        // when
        Optional<RefreshToken> refreshTokenEntity = refreshTokenRepository.findByRefreshToken(refreshToken);

        // then
        assertFalse(refreshTokenEntity.isPresent());
    }

    private void setUp_refreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(token)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
