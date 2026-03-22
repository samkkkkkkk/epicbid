package com.example.epicbid.domain.auction.dto;

import com.example.epicbid.domain.product.enums.BookCondition;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AuctionDto {

    public record BidRequest(
            @NotNull(message = "입찰 금액을 입력해주세요.")
            @DecimalMin(value = "100", message = "입찰 금액은 최소 100원 이상이어야 합니다.")
            BigDecimal bidPrice
    ) {
    }

    public record RegisterRequest(
            @NotBlank String isbn,
            @NotBlank String title,
            @NotBlank String author,
            @NotBlank String publisher,

            @NotNull BookCondition conditionGrade,

            @NotNull
            BigDecimal startPrice,

            @NotNull
            BigDecimal minPrice,

            @NotNull
            @Future(message = "경매 종료 시간은 현재 시간 이후여야 합니다.")
            LocalDateTime endTime
    ) {
    }

    public record RegisterResponse(Long auctionId, Long productId) {}

}
