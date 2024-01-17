package com.example.demo.module.refreshtoken;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.refreshtoken.in_dto.RefreshToken_inDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.UserService;
import com.example.demo.module.user.out_dto.Login_OutDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api")
public class RefreshTokenController {

    private final RefreshTokenService refreshTokenService;
    private final UserService userService;
    private final MyJwtProvider myJwtProvider;

    @PostMapping("/refreshToken")
    public ResponseEntity<?> requestRefresh(@RequestBody RefreshToken_inDTO refreshTokenInDTO) {
        log.debug(("accessToken 재발급 - POST, Controller"));

        // Redis refreshToken 검증
        RefreshToken refreshToken = refreshTokenService.findRefreshToken(refreshTokenInDTO.getRefreshToken());
        Long userId = refreshToken.getUserId();

        // Redis userId 검증
        User userEntity = userService.findUser(userId);

        // accessToken 생성
        String accessToken = myJwtProvider.createAccessToken(userEntity);

        // 응답 DTO
        Login_OutDTO loginOutDTO = Login_OutDTO.fromTokensAndUserEntity(accessToken,
                refreshTokenInDTO.getRefreshToken(), userEntity);

        return ResponseEntity.ok().body(new ResponseDTO<>().data(loginOutDTO));
    }
}
