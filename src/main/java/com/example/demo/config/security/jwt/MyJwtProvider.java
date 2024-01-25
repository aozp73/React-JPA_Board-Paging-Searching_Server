package com.example.demo.config.security.jwt;

import com.example.demo.config.envBeanConfig.jwt.MyJwtSource;
import com.example.demo.module.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;
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
        // 현재 아키텍쳐에선 사용자 정보 활용 x
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

    /**
     * 로그인 시, refreshToken Cookie 전송
     */
    public void createCookieByRefreshToken(HttpServletResponse response, String refreshToken) {

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .path("/") // 모든 곳에서 사용 허용
                .sameSite("Strict") // CSRF 공격 방지
                .httpOnly(true) // XSS 공격 방지
                .secure(false) // HTTPS 적용 프로젝트 x
                .maxAge(7 * 24 * 60 * 60) // 1주일 (refreshToken과 동일)
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
    }


    /**
     * JWT 토큰 검증
     */
    public Jws<Claims> verify(String jwt) {
        Key key = getSigningKey(accessSecret);

        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(jwt);
    }

    public static Key getSigningKey(byte[] secretKey) {
        return Keys.hmacShaKeyFor(secretKey);
    }
}
