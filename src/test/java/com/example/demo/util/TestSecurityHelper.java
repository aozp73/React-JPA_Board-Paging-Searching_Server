package com.example.demo.util;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class TestSecurityHelper {

    public static String createAccessToken(MyJwtProvider jwtProvider, Long userId, String email, String username) {
        User mockUser = User.builder()
                .id(userId)
                .email(email)
                .username(username)
                .role(UserRole.COMMON)
                .build();

        return jwtProvider.createAccessToken(mockUser);
    }

    public static String createRefreshToken(MyJwtProvider jwtProvider, Long userId, String email, String username) {
        User mockUser = User.builder()
                .id(userId)
                .email(email)
                .username(username)
                .role(UserRole.COMMON)
                .build();

        return jwtProvider.createRefreshToken(mockUser);
    }

    public static void setAuthentication() {
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setEmail("mockUser@naver.com");
        mockUser.setRole(UserRole.COMMON);
        MyUserDetails mockMyUserDetails = new MyUserDetails(mockUser);

        // SecurityContext에 Authentication 설정
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                mockMyUserDetails, null, mockMyUserDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
