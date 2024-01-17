package com.example.demo.web.user;

import com.example.demo.config.security.MyAuthenticationManagerConfig;
import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.user.UserController;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.in_dto.Login_InDTO;
import com.example.demo.module.user.out_dto.Login_OutDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({MySecurityConfig.class, MyJwtProvider.class, MyAuthenticationManagerConfig.class}) // 추가 하지 않을 경우, 기본 Security 설정 사용
@WebMvcTest(UserController.class)
public class UserLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @Test
    @DisplayName("로그인 성공")
    public void login_SuccessTest() throws Exception {
        // given
        Login_InDTO loginInDTO = Login_InDTO.builder()
                .email("test@test.com")
                .password("123456")
                .build();
        String content = new ObjectMapper().writeValueAsString(loginInDTO);

        Login_OutDTO mockLoginOutDTO = Login_OutDTO.builder()
                .accessToken("mockAccessToken")
                .refreshToken("mockRefreshToken")
                .userId(1L)
                .username("test")
                .email("test@test.com")
                .build();

        when(userService.login(any(Login_InDTO.class))).thenReturn(mockLoginOutDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.accessToken").value("mockAccessToken"))
                .andExpect(jsonPath("$.data.refreshToken").value("mockRefreshToken"))
                .andExpect(jsonPath("$.data.userId").value(1L))
                .andExpect(jsonPath("$.data.username").value("test"))
                .andExpect(jsonPath("$.data.email").value("test@test.com"))
                .andDo(MockMvcResultHandlers.print());

        verify(userService).login(any(Login_InDTO.class));
    }

    @Test
    @DisplayName("로그인 실패 - 아이디, 패스워드 입력 x")
    public void login_ValidEmailPassword_FailTest() throws Exception {
        // given
        Login_InDTO loginInDTO = Login_InDTO.builder()
                .email("")
                .password("")
                .build();
        String content = new ObjectMapper().writeValueAsString(loginInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("입력 값 확인"))
                .andExpect(jsonPath("$.data.email").value("이메일을 입력해주세요"))
                .andExpect(jsonPath("$.data.password").value("패스워드를 입력해주세요"))
                .andDo(MockMvcResultHandlers.print());

        // UserService의 login 메서드 호출 검증
        verify(userService, never()).login(any(Login_InDTO.class));
    }
}
