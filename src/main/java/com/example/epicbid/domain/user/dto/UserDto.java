package com.example.epicbid.domain.user.dto;

import com.example.epicbid.domain.user.entity.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public class UserDto {

    // 회원가입 요청 DTO
    public record SignupRequest(
            @NotBlank(message = "이메일은 필수입니다.")
            @Email(message = "올바른 이메일 형식이 아닙니다.")
            String email,

            @NotBlank(message = "비밀번호는 필수입니다.")
            String password
    ){}

    public record Response(
            Long id,
            String email,
            BigDecimal pointBalance
    ) {
        public static Response from(User user) {
            return new Response(
                    user.getId(),
                    user.getEmail(),
                    user.getPointBalance()
            );
        }
    }

    public record LoginRequest(
            @NotBlank(message = "이메을 입력해주세요.")
            @Email
            String email,

            @NotBlank(message = "비밀번호를 입력해주세요.")
            String password
    ){}

    public record LoginResult(
            String accessToken
    ){}
}
