package com.example.demo.module.refreshtoken;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@Builder
@AllArgsConstructor
@RedisHash(value = "jwtToken", timeToLive = 60*60*24*3) // 1주일
public class RefreshToken {

    @Id
    private Long userId;

    @Indexed
    private String refreshToken;
}

