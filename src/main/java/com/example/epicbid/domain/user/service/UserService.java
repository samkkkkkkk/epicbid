package com.example.epicbid.domain.user.service;

import com.example.epicbid.domain.user.dto.UserDto;
import com.example.epicbid.domain.user.entity.User;
import com.example.epicbid.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public UserDto.Response signup(UserDto.SignupRequest request) {
        // 이메일 중복 검사
        if (userRepository.existsByEmail(request.email())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 유저 엔티티 생성
        User user = User.builder()
                .email(request.email())
                .password(request.password())
                .build();

        // DB 저장 및 DTO 변환
        User saved = userRepository.save(user);
        return UserDto.Response.from(saved);
    }

    @Transactional(readOnly = true)
    public UserDto.Response getUserInfo(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
        return UserDto.Response.from(user);
    }
}
