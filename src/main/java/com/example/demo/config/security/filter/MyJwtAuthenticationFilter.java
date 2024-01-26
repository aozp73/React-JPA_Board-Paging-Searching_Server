package com.example.demo.config.security.filter;

import com.example.demo.config.security.jwt.MyJwtProvider;
import com.example.demo.config.security.principal.MyUserDetails;
import com.example.demo.module.user.User;
import com.example.demo.module.user.enums.UserRole;
import com.example.demo.util.MySecurityUtil;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
            MySecurityUtil.handleExceptionResponse(response, "Token Exception: NOT_FOUND_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (SecurityException | MalformedJwtException e) {
            MySecurityUtil.handleExceptionResponse(response, "Token Exception: INVALID_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (ExpiredJwtException e) {
            MySecurityUtil.handleExceptionResponse(response, "Token Exception: EXPIRED_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (UnsupportedJwtException e) {
            MySecurityUtil.handleExceptionResponse(response, "Token Exception: UNSUPPORTED_TOKEN", HttpServletResponse.SC_BAD_REQUEST);

        } catch (Exception e) {
            log.error("====================================================");
            log.error("JwtFilter - doFilterInternal() ETCException");
            log.error("token : {}", token);
            log.error("Exception Message : ", e);
            log.error("====================================================");

            MySecurityUtil.handleExceptionResponse(response, "Token Exception: JWT ETCException", HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getToken(HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (StringUtils.hasText(authorization) && authorization.startsWith("Bearer")){
            String[] arr = authorization.split(" ");
            System.out.println("arr = " + arr[1]);
            return arr[1];
        }
        return null;
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
}

