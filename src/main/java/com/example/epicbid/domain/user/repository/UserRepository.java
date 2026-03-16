package com.example.epicbid.domain.user.repository;

import com.example.epicbid.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.swing.text.html.Option;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    // 이메일 중복 검사용
    boolean existsByEmail(String email);

    // 로그인 시 이메일로 회원 조회용
    Optional<User> findByEmail(String email);
}


