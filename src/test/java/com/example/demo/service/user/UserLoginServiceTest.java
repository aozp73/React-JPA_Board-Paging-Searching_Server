package com.example.demo.service.user;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.refreshtoken.RefreshToken;
import com.example.demo.module.refreshtoken.RefreshTokenRepository;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserRepository;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.in_dto.Login_InDTO;
import com.example.demo.module.user.in_dto.Login_OutDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserLoginServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;
    @Mock
    private MyJwtProvider myJwtProvider;
    @Mock
    private RefreshTokenRepository refreshTokenRepository;
    @Mock
    private StringRedisTemplate stringRedisTemplate;
    @Mock
    private ValueOperations<String, String> valueOperations;

    @BeforeEach
    void setUp() {
    }

    @Test
    @DisplayName("로그인 성공")
    public void login_SuccessTest() {
        // given
        Login_InDTO loginInDTO = new Login_InDTO("test@test.com", "123456");
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("test@test.com");
        mockUser.setPassword(passwordEncoder.encode("123456"));

        when(stringRedisTemplate.opsForValue()).thenReturn(valueOperations);
        doNothing().when(valueOperations).set(anyString(), anyString());

        when(userRepository.findByEmail(loginInDTO.getEmail())).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginInDTO.getPassword(), mockUser.getPassword())).thenReturn(true);
        when(myJwtProvider.createAccessToken(mockUser)).thenReturn("accessToken");
        when(myJwtProvider.createRefreshToken(mockUser)).thenReturn("refreshToken");

        // when
        Login_OutDTO result = userService.login(loginInDTO);

        // then
        assertEquals("accessToken", result.getAccessToken());
        assertEquals("refreshToken", result.getRefreshToken());
        verify(userRepository).findByEmail(loginInDTO.getEmail());
        verify(passwordEncoder).matches(loginInDTO.getPassword(), mockUser.getPassword());
        verify(myJwtProvider).createAccessToken(mockUser);
        verify(myJwtProvider).createRefreshToken(mockUser);
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }
}
