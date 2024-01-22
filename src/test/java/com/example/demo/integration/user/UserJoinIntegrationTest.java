package com.example.demo.integration.user;

import com.example.demo.AbstractIntegrationTest;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.module.user.in_dto.Join_InDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureRestDocs(uriScheme = "http", uriHost = "localhost", uriPort = 8080)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = WebEnvironment.MOCK)
public class UserJoinIntegrationTest extends AbstractIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EntityManager em;

    @Autowired private UserRepository userRepository;
    @Autowired private BCryptPasswordEncoder bCryptPasswordEncoder;

    @BeforeEach
    public void init() {
        // rollBack_AutoIncrement
        em.createNativeQuery("ALTER TABLE user_tb ALTER COLUMN ID RESTART WITH 1").executeUpdate();
    }

    @Test
    @DisplayName("회원가입 성공")
    public void join_success() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("abc@test.com")
                .password("123456")
                .passwordConfirmation("123456")
                .username("123")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(content)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andDo(MockMvcResultHandlers.print());
        resultActions.andDo(MockMvcResultHandlers.print()).andDo(document);


        Optional<User> savedUser = userRepository.findByEmail("abc@test.com");

        assertTrue(savedUser.isPresent());
        assertTrue(bCryptPasswordEncoder.matches("123456", savedUser.get().getPassword()));
        assertEquals(1L, savedUser.get().getId());
        assertEquals("123", savedUser.get().getUsername());
        assertEquals("abc@test.com", savedUser.get().getEmail());
        assertEquals(UserRole.COMMON, savedUser.get().getRole());
    }

    @Test
    @DisplayName("회원가입 실패 - password 자릿 수")
    public void join_fail_valid_password() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("abc@test.com")
                .password("1234") // @Size(min = 6, max = 20, message = "6글자 이상 20자 이내로 입력해주세요")
                .passwordConfirmation("1234")
                .username("123")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
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

        assertFalse(userRepository.findByEmail("abc@test.com").isPresent());
    }

    @Test
    @DisplayName("회원가입 실패 - username,password 자릿 수")
    public void join_fail_valid_password_username() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("abc@test.com")
                .password("1234") // @Size(min = 6, max = 20, message = "6글자 이상 20자 이내로 입력해주세요")
                .passwordConfirmation("1234")
                .username("1234567") // @Size(max = 6, message = "6글자 이내로 입력해주세요")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
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

        assertFalse(userRepository.findByEmail("abc@test.com").isPresent());
    }

    @Test
    @DisplayName("회원가입 실패 - 공백")
    public void join_fail_blank() throws Exception {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("") // @NotBlank(message = "이메일을 입력해주세요")
                .password("") // @NotBlank(message = "패스워드를 입력해주세요")
                .passwordConfirmation("") // @NotBlank(message = "패스워드 확인을 입력해주세요")
                .username("") // @NotBlank(message = "아이디를 입력해주세요")
                .build();
        String content = new ObjectMapper().writeValueAsString(joinInDTO);

        // when
        ResultActions resultActions = mockMvc.perform(post("/api/join")
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
                .andExpect(jsonPath("$.data.passwordConfirmation").value("패스워드 확인을 입력해주세요"))
                .andExpect(jsonPath("$.data.username").value("아이디를 입력해주세요"))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("이메일 체크 통합 테스트 - 성공")
    public void emailCheck_success() throws Exception {
        // given
        String email = "abc@test.com";

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/emailCheck?email=" + email)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").value(true))
                .andDo(MockMvcResultHandlers.print());
    }

    @Test
    @DisplayName("이메일 체크 통합 테스트 - 실패")
    public void emailCheck_fail() throws Exception {
        // given
        User user = User.builder()
                .email("abc@test.com")
                .username("123")
                .password("1234")
                .role(UserRole.COMMON)
                .createdAt(LocalDateTime.now())
                .build();
        em.merge(user);
        em.flush();
        em.clear();

        String email = "abc@test.com";

        // when
        ResultActions resultActions = mockMvc.perform(get("/api/emailCheck?email=" + email)
                .accept(MediaType.APPLICATION_JSON));

        // then
        resultActions
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.msg").value("성공"))
                .andExpect(jsonPath("$.data").value(false))
                .andDo(MockMvcResultHandlers.print());
    }
}
