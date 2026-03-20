package com.example.epicbid.domain.user.entity;

import com.example.epicbid.domain.user.emums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal pointBalance = BigDecimal.ZERO;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.USER;

    @Builder
    public User(String email, String password, Role role) {
        this.email = email;
        this.password = password;
        this.role = role != null ? role : Role.USER;
        this.pointBalance = BigDecimal.ZERO;
    }

    // 포인트 충전 메서드
    public void addPoints(BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
        this.pointBalance = this.pointBalance.add(amount);
    }

    // 포인트 차감 메서드
    public void deductPoints(BigDecimal amount) {
        if (this.pointBalance.compareTo(amount) < 0) {
            throw new IllegalStateException("포인트가 부족합니다.");
        }
        this.pointBalance = this.pointBalance.subtract(amount);
    }
}
