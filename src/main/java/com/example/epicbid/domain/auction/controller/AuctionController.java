package com.example.epicbid.domain.auction.controller;

import com.example.epicbid.domain.auction.dto.AuctionDto;
import com.example.epicbid.domain.auction.service.AuctionService;
import com.example.epicbid.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auctions")
@RequiredArgsConstructor
public class AuctionController {

    private final AuctionService auctionService;

    // 경매 등록 API
    @PostMapping
    public ResponseEntity<AuctionDto.RegisterResponse> registerAuction(
            @Valid @RequestBody AuctionDto.RegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        AuctionDto.RegisterResponse response = auctionService.registerAuction(request, userDetails.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 경매 API
    @PostMapping("/{auctionId}/bids")
    public ResponseEntity<?> bid(
            @PathVariable Long auctionId,
            @Valid @RequestBody AuctionDto.BidRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        auctionService.bid(auctionId, userDetails.userId(), request);
        return ResponseEntity.ok().body("입찰 성공");
    }
}
