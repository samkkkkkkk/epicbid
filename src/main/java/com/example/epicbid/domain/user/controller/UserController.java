package com.example.epicbid.domain.user.controller;

import com.example.epicbid.domain.user.dto.UserDto;
import com.example.epicbid.domain.user.service.UserService;
import com.example.epicbid.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto.Response> signup(@Valid @RequestBody UserDto.SignupRequest request) {
        UserDto.Response response = userService.signup(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto.LoginResult> login(@Valid @RequestBody UserDto.LoginRequest request) {
        UserDto.LoginResult result = userService.login(request);
        return ResponseEntity.ok().body(result);
    }


    // 내 정보 조회
    @GetMapping("/me")
    public ResponseEntity<UserDto.Response> getUserInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        UserDto.Response response = userService.getUserInfo(userDetails.userId());
        return ResponseEntity.ok(response);
    }


}
