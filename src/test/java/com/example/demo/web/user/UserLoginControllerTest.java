package com.example.demo.web.user;

import com.example.demo.config.security.MySecurityConfig;
import com.example.demo.module.user.UserController;
import com.example.demo.module.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

@Import(MySecurityConfig.class) // 추가 하지 않을 경우, 기본 Security 설정 사용
@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(UserController.class)
public class UserLoginControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


}
