package com.example.demo.config.security.filter;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.exception.ResponseDTO;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class MyJwtAuthenticationFilter extends OncePerRequestFilter {

    private final MyJwtProvider myJwtProvider;
    private final ObjectMapper om = new ObjectMapper();

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {
        String token = "";
        try {
            token = getToken(request);
            if (StringUtils.hasText(token)) {
                setAuthentication(token);
            }
            filterChain.doFilter(request, response);
        }
        catch (NullPointerException | IllegalStateException e) {
            handleExceptionResponse(response, "Token Exception: NOT_FOUND_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (SecurityException | MalformedJwtException e) {
            handleExceptionResponse(response, "Token Exception: INVALID_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (ExpiredJwtException e) {
            handleExceptionResponse(response, "Token Exception: EXPIRED_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (UnsupportedJwtException e) {
            handleExceptionResponse(response, "Token Exception: UNSUPPORTED_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (Exception e) {
            log.error("====================================================");
            log.error("JwtFilter - doFilterInternal() ETCException");
            log.error("token : {}", token);
            log.error("Exception Message : {}", e.getMessage());
            log.error("====================================================");

            handleExceptionResponse(response, "Token Exception: JWT ETCException", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void setAuthentication(String token) {
        Jws<Claims> jws = myJwtProvider.verify(token);
        Claims claims = jws.getBody();
        Long userId = claims.get("userId", Long.class);
        String username = claims.get("username", String.class);
        String role = claims.get("role", String.class);

        User user = User.builder().id(userId).username(username).role(UserRole.valueOf(role)).build();
        MyUserDetails myUserDetails = new MyUserDetails(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                myUserDetails,
                null,
                myUserDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer")){
            String[] arr = authorization.split(" ");
            return arr[1];
        }
        return null;
    }

    private void handleExceptionResponse(HttpServletResponse response, String errorMessage, int statusCode) throws IOException {
        log.error(errorMessage);

        ResponseDTO<?> errorResponse = new ResponseDTO<>().fail(HttpStatus.valueOf(statusCode), errorMessage, null);
        response.setStatus(statusCode);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        response.getWriter().write(om.writeValueAsString(errorResponse));
    }
}

