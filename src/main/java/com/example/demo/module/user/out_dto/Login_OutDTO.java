package com.example.demo.module.user.out_dto;

import com.example.demo.module.user.User;
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


    public static Login_OutDTO fromTokensAndUserEntity(String accessToken, String refreshToken, User userEntity) {
        return Login_OutDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .build();
    }
}
