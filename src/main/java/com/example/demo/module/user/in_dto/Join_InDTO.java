package com.example.demo.module.user.in_dto;

import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
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
    @NotBlank(message = "이메일을 입력해주세요")
    private String email; // 로그인

    @NotBlank(message = "패스워드를 입력해주세요")
    @Size(min = 6, max = 20, message = "6글자 이상 20자 이내로 입력해주세요")
    private String password;
    @NotBlank(message = "패스워드 확인을 입력해주세요")
    private String passwordConfirmation;

    @NotBlank(message = "아이디를 입력해주세요")
    @Size(max = 6, message = "6글자 이내로 입력해주세요")
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
