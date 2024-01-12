package com.example.demo.module.user.in_dto;

import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Login_OutDTO {
    private String accessToken;
    private String refreshToken;

    private Long userId;
    private String username;
    private String email;
}
