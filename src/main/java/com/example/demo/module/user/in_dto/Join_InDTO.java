package com.example.demo.module.user.in_dto;

import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.module.user.valid.join_InDTO.ValidPassword;
import com.example.demo.module.user.valid.join_InDTO.ValidUsername;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Join_InDTO {
    /**
     * [@Validxxx 및 ConstraintValidator 사용]
     * - 공백 요청 시, valid message 순서 제어
     */

    @NotBlank(message = "이메일을 입력해주세요")
    private String email; // 로그인

    @ValidPassword
    private String password;
    @NotBlank(message = "패스워드 확인을 입력해주세요")
    private String passwordConfirmation;

    @ValidUsername
    private String username;

    public User toEntity(BCryptPasswordEncoder passwordEncoder) {
        return User.builder()
                .email(email)
                .password(passwordEncoder.encode(password))
                .username(username)
                .role(UserRole.COMMON)
                .createdAt(LocalDateTime.now())
                .build();
    }
}
