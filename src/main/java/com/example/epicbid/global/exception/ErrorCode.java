package com.example.epicbid.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // User 관련 에러
    EMAIL_ALREADY_EXISTS(HttpStatus.CONFLICT, "U001", "이미 사용 중인 이메일입니다."),
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "U002", "사용자를 찾을 수 없습니다."),
    INVALID_PASSWORD(HttpStatus.UNAUTHORIZED, "U003", "인증 정보가 일치하지 않습니다."),

    EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "S002", "만료된 토큰입니다."),
    INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "S001", "유효하지 않은 토큰입니다."),


    INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST, "C001", "잘못된 입력값입니다."),
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "C002", "서버 내부 요류가 발생했습니다."),

    // Auction 관련 에러
    INVALID_AUCTION(HttpStatus.BAD_REQUEST, "A001", "진행중인 경매가 아닙니다."),
    AUCTION_NOT_FOUND(HttpStatus.NOT_FOUND, "A002", "경매를 찾을 수 없습니다."),
    AUCTION_INVALID_INPUT(HttpStatus.BAD_REQUEST, "A003", "현재 최고 입찰가보다 높은 금액을 제시해야 합니다."),

    // Product 관련 에러
    INVALID_CONDITION(HttpStatus.BAD_REQUEST, "P001", "중고 상품은 새 상품으로 등록할 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "P002", "상품을 찾을 수 없습니다.");

    private final HttpStatus status;
    private final String code;
    private final String message;
}
