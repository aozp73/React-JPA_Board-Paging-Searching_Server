package com.example.demo.integration.refreshToken;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.refreshtoken.RefreshToken;
import com.example.demo.module.refreshtoken.RefreshTokenRepository;
import com.example.demo.module.refreshtoken.in_dto.RefreshToken_inDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.DummyEntityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

import javax.persistence.EntityManager;
import javax.servlet.http.Cookie;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class RequestRefreshTokenIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;

    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired private MyJwtProvider myJwtProvider;

    private static RedisServer redisServer;

    @BeforeEach
    public void init() {
        // rollBack_EmbeddedRedis
        refreshTokenRepository.deleteAll();

        // rollBack_AutoIncrement
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();

        /**
         * [초기 데이터 및 Save]
         * - User Entity 1건
         * - RefreshToken Entity 1건
         */
        DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        setUp_refreshToken(1L, "mockToken");

        em.flush();
        em.clear();
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
    @DisplayName("RefreshToken 요청 성공")
    public void requestRefresh_SuccessTest() throws Exception {
        // given
        User userEntity = DummyEntityHelper.setUpUser(em, "user1@naver.com", "user1", "abc1", UserRole.COMMON);
        String refreshToken = myJwtProvider.createRefreshToken(userEntity);
        setUp_refreshToken(1L, refreshToken);
        em.flush();
        em.clear();

        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.username").value("user1"))
                .andExpect(jsonPath("$.data.email").value("user1@naver.com"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("RefreshToken 요청 실패 - 잘못된 토큰 전송")
    public void requestRefresh_RefreshTokenValid_FailTest() throws Exception {
        // given
        Cookie cookie = new Cookie("refreshToken", "wrong");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/refreshToken")
                .contentType(MediaType.APPLICATION_JSON)
                .cookie(cookie)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.data").value("RefreshToken 오류 (만료, 잘못된 형식 등, 재로그인 필요)"))
                .andDo(MockMvcResultHandlers.print());
    }

    private void setUp_refreshToken(Long userId, String token) {
        RefreshToken refreshToken = RefreshToken.builder()
                .userId(userId)
                .refreshToken(token)
                .build();

        refreshTokenRepository.save(refreshToken);
    }
}
