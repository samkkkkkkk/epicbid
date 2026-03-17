package com.example.epicbid.domain.user.controller;

import com.example.epicbid.domain.user.dto.UserDto;
import com.example.epicbid.domain.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    @GetMapping("/{userId}")
    public ResponseEntity<UserDto.Response> getUserInfo(@PathVariable Long userId) {
        UserDto.Response response = userService.getUserInfo(userId);
        return ResponseEntity.ok(response);
    }
}
