package com.example.demo.module.refreshtoken;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.exception.statuscode.Exception400;
import com.example.demo.module.refreshtoken.out_dto.AccessTokenReissue_OutDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.out_dto.Login_OutDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final MyJwtProvider myJwtProvider;

    @GetMapping("/refreshToken")
    public ResponseEntity<?> requestRefresh(HttpServletRequest request) {
        log.debug(("accessToken 재발급 - POST, Controller"));

        // refreshToken 추출 및 값 확인
        String refreshTokenValue = myJwtProvider.getRefreshTokenByCookie(request);
        if (refreshTokenValue == null) {
            throw new Exception400("RefreshToken이 전송되지 않았습니다. (재로그인 필요)");
        }

        // refreshToken 만료 확인
        if (myJwtProvider.isRefreshTokenExpired(refreshTokenValue)) {
            throw new Exception400("RefreshToken 오류 (만료, 잘못된 형식 등, 재로그인 필요)");
        }

        // Redis 내 refreshToken 유무 확인
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenValue);
        Long userId = refreshToken.getUserId();

        // Redis userId 검증
        User userEntity = userService.findUser(userId);

        // accessToken 생성
        String accessToken = myJwtProvider.createAccessToken(userEntity);


        return ResponseEntity.ok().body(
                new ResponseDTO<>().data(AccessTokenReissue_OutDTO.fromTokensAndUserEntity(accessToken, userEntity))
        );
    }
}
