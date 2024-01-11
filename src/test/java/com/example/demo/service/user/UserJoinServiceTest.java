package com.example.demo.service.user;

import com.example.demo.exception.statuscode.Exception400;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.in_dto.Join_InDTO;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserJoinServiceTest {

    @InjectMocks // 해당 파일에 있는 @Mock Bean 주입
    private UserService userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Test
    @DisplayName("회원가입 성공")
    public void joinTest_Success() {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("test@test.com")
                .password("123456")
                .passwordConfirmation("123456")
                .username("1234")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // when
        userService.save(joinInDTO);

        // then
        verify(userRepository).save(any(User.class)); // userRepository save() 호출 확인
    }

    @Test
    @DisplayName("회원가입 실패 - 동일 이메일 존재")
    public void joinExceptionTest_EmailExists() {
        // given
        Join_InDTO joinInDTO = Join_InDTO.builder()
                .email("test@test.com")
                .password("123456")
                .passwordConfirmation("123456")
                .username("1234")
                .build();

        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(new User()));

        // when & then
        assertThrows(Exception400.class, () -> userService.save(joinInDTO));
        verify(userRepository, never()).save(any(User.class));
    }
}
