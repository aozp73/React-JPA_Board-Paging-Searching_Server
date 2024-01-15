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

        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}
