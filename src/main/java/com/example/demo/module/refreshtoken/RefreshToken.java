package com.example.demo.module.refreshtoken;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@AllArgsConstructor
@Getter
@RedisHash(value = "jwtToken", timeToLive = 60*60*24*3) // 1주일
public class RefreshToken {

    @Id
    private Long userId;

    private String refreshToken;
}
