package com.example.epicbid.domain.user.service;

import com.example.epicbid.domain.user.dto.UserDto;
import com.example.epicbid.domain.user.entity.User;
import com.example.epicbid.domain.user.repository.UserRepository;
import com.example.epicbid.global.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Transactional
    public UserDto.Response signup(UserDto.SignupRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }


        // 유저 엔티티 생성
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .build();

        // DB 저장 및 DTO 변환
        User saved = userRepository.save(user);
        return UserDto.Response.from(saved);
    }

    @Transactional(readOnly = true)
    public UserDto.LoginResult login(UserDto.LoginRequest request) {

        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일 입니다."));

        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 검증 성공 시 JWT 토큰 생성 및 발급
        return new UserDto.LoginResult(jwtUtil.generateToke(user.getEmail(), user.getId()));

    }

    @Transactional(readOnly = true)
    public UserDto.Response getUserInfo(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserDto.Response.from(user);
    }
}
