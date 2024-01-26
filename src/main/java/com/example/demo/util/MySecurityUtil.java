package com.example.demo.util;

import com.example.demo.exception.ResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MySecurityUtil {

    private static final ObjectMapper om = new ObjectMapper();

    public static void handleExceptionResponse(HttpServletResponse response, String errorMessage, int statusCode) throws IOException {
        log.error(errorMessage);

        ResponseDTO<?> errorResponse = new ResponseDTO<>().fail(HttpStatus.valueOf(statusCode), errorMessage, null);
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        /**
         * Spring MVC의 Exception Advice
         * - Spring MVC의 기본 CORS 처리 적용,
         *   MySecurityConfig에서 정의한 CORS 설정이 예외 응답에 자동으로 적용
         *
         * 현재 위치 (Filter)
         * - Spring Security 설정 적용 x,수동으로 설정해서 응답해야한다.
         * - 이렇게 해야 제대로 된 응답을 받고,
         *   AccessToken이 만료되었을 경우엔 Client가 RefreshToken을 포함한 요청을 이어갈 수 있다.
         */
        response.setHeader("Access-Control-Allow-Origin", "http://localhost:3000");
        response.setHeader("Access-Control-Allow-Credentials", "true");

        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}
