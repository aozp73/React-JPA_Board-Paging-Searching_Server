package com.example.demo.module.user;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.exception.statuscode.Exception400;
import com.example.demo.module.refreshtoken.in_dto.RefreshToken_inDTO;
import com.example.demo.module.user.in_dto.Join_InDTO;
import com.example.demo.module.user.in_dto.Login_InDTO;
import com.example.demo.module.user.out_dto.Login_OutDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

@RestController
@RequiredArgsConstructor @Slf4j
@RequestMapping("/api")
public class UserController {

    private final UserService userService;
    private final MyJwtProvider myJwtProvider;

    @PostMapping("/join")
    public ResponseEntity<?> join(@RequestBody @Valid Join_InDTO joinInDTO) {
        log.debug("회원가입 요청 - POST, Controller)");
        userService.save(joinInDTO);

        return ResponseEntity.ok().body(new ResponseDTO<>());
    }

    @GetMapping("/emailCheck")
    public ResponseEntity<?> emailCheck(@RequestParam String email) {
        log.debug("회원가입 입력 중 이메일 체크 - POST, Controller");
        boolean isValid = userService.emailCheck(email);

        return ResponseEntity.ok().body(new ResponseDTO<>().data(isValid));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody @Valid Login_InDTO loginInDTO, HttpServletResponse response) {
        log.debug(("로그인 요청 - POST, Controller"));
        // Redis 저장: RefreshToken (AccessToken 재발급 검증)
        // 응답 데이터: AccessToken / userId (클라이언트 수정,삭제 렌더링) / username (Header 렌더링) / email
        Login_OutDTO loginOutDTO = userService.login(loginInDTO);

        // Set-Cookie: RefreshToken
        myJwtProvider.createCookieByRefreshToken(response, loginOutDTO.getRefreshToken());

        return ResponseEntity.ok().body(new ResponseDTO<>().data(loginOutDTO));
    }

    @DeleteMapping("/auth/logout")
    public ResponseEntity<?> logout(HttpServletRequest request,
                                    @AuthenticationPrincipal MyUserDetails myUserDetails) {
        log.debug(("로그아웃 요청 - DELETE, Controller"));

        // refreshToken 추출 및 값 확인
        String refreshTokenValue = myJwtProvider.getRefreshTokenByCookie(request);
        if (refreshTokenValue == null) {
            throw new Exception400("RefreshToken이 전송되지 않았습니다. (재로그인 필요)");
        }

        // refreshToken 만료 확인
        if (myJwtProvider.isRefreshTokenExpired(refreshTokenValue)) {
            throw new Exception400("RefreshToken 오류 (만료, 잘못된 형식 등, 재로그인 필요)");
        }

        userService.deleteRefreshToken(refreshTokenValue, myUserDetails.getUser().getId());

        return ResponseEntity.ok().body(new ResponseDTO<>());
    }
}
