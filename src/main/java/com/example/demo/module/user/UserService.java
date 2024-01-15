package com.example.demo.module.user;

import com.example.demo.exception.statuscode.Exception400;
import com.example.demo.exception.statuscode.Exception500;
import com.example.demo.module.refreshtoken.RefreshToken;
import com.example.demo.module.refreshtoken.RefreshTokenRepository;
import com.example.demo.module.user.in_dto.Join_InDTO;
import com.example.demo.module.user.in_dto.Login_InDTO;
import com.example.demo.module.user.in_dto.Login_OutDTO;
import com.example.demo.config.security.jwt.MyJwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor @Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    private final StringRedisTemplate stringRedisTemplate;
    private final BCryptPasswordEncoder passwordEncoder;
    private final MyJwtProvider myJwtProvider;

    @Transactional
    public void save(Join_InDTO joinInDTO) {
        log.debug("회원가입 요청- POST, Service");

        if(userRepository.findByEmail(joinInDTO.getEmail()).isPresent()) {
            throw new Exception400("동일 이메일 존재");
        }

        try {
            userRepository.save(joinInDTO.toEntity(passwordEncoder));
        } catch (Exception exception) {
            throw new Exception500("회원가입 실패");
        }
    }

    @Transactional(readOnly = true)
    public Boolean emailCheck(String email) {
        log.debug("회원가입 입력 중 이메일 체크 - POST, Service");

        return userRepository.findByEmail(email).isEmpty();
    }

    @Transactional(readOnly = true)
    public Login_OutDTO login(Login_InDTO loginInDTO) {
        log.debug(("로그인 요청 - POST, Service"));

        // 검증 - 로그인 요청 값
        User userEntity = userRepository.findByEmail(loginInDTO.getEmail()).orElseThrow(
                () -> new Exception400("이메일을 다시 확인해주세요.")
        );
        boolean matches = passwordEncoder.matches(loginInDTO.getPassword(), userEntity.getPassword());
        if (!matches) {
            throw new Exception400("비밀번호를 다시 확인해주세요.");
        };

        // 토큰 생성 - access, refresh 토큰
        String accessToken = myJwtProvider.createAccessToken(userEntity);
        String refreshToken = myJwtProvider.createRefreshToken(userEntity);

        // Redis 저장 - refresh 토큰
        refreshTokenRepository.save(new RefreshToken(userEntity.getId(), refreshToken));
        stringRedisTemplate.opsForValue().set("refreshTokenIndex:" + refreshToken, userEntity.getId().toString());

        // DTO 응답
        return Login_OutDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .userId(userEntity.getId())
                .email(userEntity.getEmail())
                .username(userEntity.getUsername())
                .build();
    }

    @Transactional
    public void deleteRefreshToken(String refreshToken, Long principalUserId) {
        log.debug(("로그아웃 요청 - DELETE, Service"));

        String userId = stringRedisTemplate.opsForValue().get("refreshTokenIndex:" + refreshToken);

//        if (userId != null && !userId.equals(principalUserId.toString())) {
//            throw new Exception401("잘못된 접근입니다.");
//        }

        if (userId != null) {
            refreshTokenRepository.deleteById(Long.parseLong(userId));
            stringRedisTemplate.delete("refreshTokenIndex:" + refreshToken);
        }

    }
}
