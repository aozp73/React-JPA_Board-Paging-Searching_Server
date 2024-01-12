package com.example.demo.util.jwt;

import com.example.demo.config.envBeanConfig.jwt.MyJwtSource;
import com.example.demo.module.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
public class MyJwtProvider {

    private final byte[] accessSecret;
    private final byte[] refreshSecret;

    public final static Long ACCESS_TOKEN_EXPIRE_COUNT = 60 * 60 * 1000L; // 1시간
    public final static Long REFRESH_TOKEN_EXPIRE_COUNT = 7 * 24 * 60 * 60 * 1000L; // 1주일

    public MyJwtProvider(MyJwtSource myJwtSource) {
        this.accessSecret = myJwtSource.getAccessKey().getBytes(StandardCharsets.UTF_8);
        this.refreshSecret = myJwtSource.getRefreshKey().getBytes(StandardCharsets.UTF_8);
    }

    /**
     * 생성 - access token / refresh token
     */
    public String createAccessToken(User userEntity) {
        return createToken(userEntity.getId(), userEntity.getEmail(), userEntity.getUsername(),
                userEntity.getRole().toString(), ACCESS_TOKEN_EXPIRE_COUNT, accessSecret);
    }
    public String createRefreshToken(User userEntity) {
        return createToken(userEntity.getId(), userEntity.getEmail(), userEntity.getUsername(),
                userEntity.getRole().toString(), REFRESH_TOKEN_EXPIRE_COUNT, refreshSecret);
    }

    private String createToken(Long id, String email, String username, String role,
                               Long expire, byte[] secretKey) {
        Claims claims = Jwts.claims().setSubject(email);
        claims.put("userId", id);
        claims.put("username", username);
        claims.put("role", role);

        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + expire))
                .signWith(getSigningKey(secretKey))
                .compact();
    }

    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }


    /**
     * 추출 - 토큰 페이로드
     */
    public Long getUserIdFromToken(String token) {
        String[] tokenArr = token.split(" ");
        token = tokenArr[1];
        Claims claims = parseToken(token, accessSecret);
        return Long.valueOf((Integer)claims.get("userId"));
    }

    public Claims parseAccessToken(String accessToken) {
        return parseToken(accessToken, accessSecret);
    }

    public Claims parseRefreshToken(String refreshToken) {
        return parseToken(refreshToken, refreshSecret);
    }


    public Claims parseToken(String token, byte[] secretKey) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey(secretKey))
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
