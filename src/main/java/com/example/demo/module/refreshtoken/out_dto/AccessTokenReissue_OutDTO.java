package com.example.demo.module.refreshtoken.out_dto;

import com.example.demo.module.user.User;
import com.example.demo.module.user.out_dto.Login_OutDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessTokenReissue_OutDTO {
    private String accessToken;

    private Long userId;
    private String username;
    private String email;

    public static AccessTokenReissue_OutDTO fromTokensAndUserEntity(String accessToken, User userEntity) {
        return AccessTokenReissue_OutDTO.builder()
                .accessToken(accessToken)
                .userId(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .build();
    }
}
