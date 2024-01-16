package com.example.demo.util;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;

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
}
