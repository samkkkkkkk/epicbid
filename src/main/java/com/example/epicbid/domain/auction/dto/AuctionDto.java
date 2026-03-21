package com.example.epicbid.domain.auction.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AuctionDto {

    public record BidRequest (
            @NotNull(message = "입찰 금액을 입력해주세요.")
            @DecimalMin(value = "100", message = "입찰 금액은 최소 100원 이상이어야 합니다.")
            BigDecimal bidPrice
    ){}
}
