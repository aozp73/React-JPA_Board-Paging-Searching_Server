package com.example.demo.integration.user;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.refreshtoken.RefreshToken;
import com.example.demo.module.refreshtoken.RefreshTokenRepository;
import com.example.demo.module.refreshtoken.in_dto.RefreshToken_inDTO;
import com.example.demo.util.TestSecurityHelper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;
import redis.embedded.RedisServer;

import javax.persistence.EntityManager;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
public class UserLogoutIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired private RefreshTokenRepository refreshTokenRepository;
    @Autowired private MyJwtProvider myJwtProvider;
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
    @DisplayName("로그아웃 성공")
    public void logout_SuccessTest() throws Exception {
        // given
        RefreshToken_inDTO mockRefreshTokenInDTO = RefreshToken_inDTO.builder()
                .refreshToken("mockToken")
                .build();
        String content = new ObjectMapper().writeValueAsString(mockRefreshTokenInDTO);

        String accessToken = TestSecurityHelper.createAccessToken(myJwtProvider, 1L, "test@test.com", "testUser");

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print());

    }

    @Test
    @DisplayName("로그아웃 실패 - Redis에 없는 AccessToken")
    public void findByRefreshToken_WrongToken_FailTest() throws Exception {
        // given
        RefreshToken_inDTO mockRefreshTokenInDTO = RefreshToken_inDTO.builder()
                .refreshToken("wrongToken")
                .build();
        String content = new ObjectMapper().writeValueAsString(mockRefreshTokenInDTO);

        String accessToken = TestSecurityHelper.createAccessToken(myJwtProvider, 1L, "test@test.com", "testUser");

        // when
        ResultActions resultActions = mockMvc.perform(delete("/api/auth/logout")
                .header("Authorization", "Bearer " + accessToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.msg").value("unAuthorized"))
                .andExpect(jsonPath("$.data").value("잘못된 접근입니다."))
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
