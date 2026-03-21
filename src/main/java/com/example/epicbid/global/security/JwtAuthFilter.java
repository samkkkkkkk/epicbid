package com.example.epicbid.global.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 요청 헤더에서 Authorization 키값 추출
        String authHeader = request.getHeader("Authorization");

        // 토큰이 존재하고 'Bearer '로 시작하는지 확인
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            // 토큰 유효성 검사
            jwtUtil.validateToken(token);

            Claims claims = jwtUtil.getClaims(token);
            Long userId = claims.get("userId", Long.class);
            String email = claims.getSubject();
            String role = claims.get("role", String.class);

            // 인증된 사용자 객체 생성
            CustomUserDetails userDetails = new CustomUserDetails(userId, email);

            // SecurityContext에 인증 정보 저장
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, List.of(new SimpleGrantedAuthority("ROLE_" + role))
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

        }


        // 다음 필터로 요청 넘기기
        filterChain.doFilter(request, response);
    }
}
