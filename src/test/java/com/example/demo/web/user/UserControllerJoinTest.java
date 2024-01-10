package com.example.demo.web.user;

import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.module.user.UserController;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.in_dto.Join_InDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import(MySecurityConfig.class) // 추가 하지 않을 경우, 기본 Security 설정 사용
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserControllerJoinTest {

    @Autowired private MockMvc mockMvc;

    @MockBean private UserService userService;

    @Test
    @DisplayName("회원가입 성공")
    public void joinTest() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("test@test.com")
                .password("123456")
                .passwordConfirmation("123456")
                .username("1234")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcResultHandlers.print());

        verify(userService).save(any(Join_InDTO.class));
    }

    @Test
    @DisplayName("회원가입 실패 - password 자릿 수")
    public void joinValidTest_password() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("test@test.com")
                .password("1234") // @Size(min = 6, max = 20, message = "6글자 이상 20자 이내로 입력해주세요")
                .passwordConfirmation("1234")
                .username("test")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("입력 값 확인"))
                .andExpect(jsonPath("$.data.password").value("6글자 이상 20자 이내로 입력해주세요"))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, never()).save(any(Join_InDTO.class));
    }

    @Test
    @DisplayName("회원가입 실패 - username 자릿 수")
    public void joinValidTest_username() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("test@test.com")
                .password("123456")
                .passwordConfirmation("123456")
                .username("1234567") // @Size(max = 6, message = "6글자 이내로 입력해주세요")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("입력 값 확인"))
                .andExpect(jsonPath("$.data.username").value("6글자 이내로 입력해주세요"))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, never()).save(any(Join_InDTO.class));
    }

    @Test
    @DisplayName("회원가입 실패 - username,password 자릿 수")
    public void joinValidTest_password_username() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("test@test.com")
                .password("1234") // @Size(min = 6, max = 20, message = "6글자 이상 20자 이내로 입력해주세요")
                .passwordConfirmation("1234")
                .username("1234567") // @Size(max = 6, message = "6글자 이내로 입력해주세요")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.msg").value("입력 값 확인"))
                .andExpect(jsonPath("$.data.password").value("6글자 이상 20자 이내로 입력해주세요"))
                .andExpect(jsonPath("$.data.username").value("6글자 이내로 입력해주세요"))
                .andDo(MockMvcResultHandlers.print());

        verify(userService, never()).save(any(Join_InDTO.class));
    }
}
