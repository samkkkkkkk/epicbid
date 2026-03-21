package com.example.epicbid.domain.auction.service;

import com.example.epicbid.domain.auction.dto.AuctionDto;
import com.example.epicbid.domain.auction.entity.Auction;
import com.example.epicbid.domain.auction.repository.AuctionRepository;
import com.example.epicbid.domain.user.entity.User;
import com.example.epicbid.domain.user.repository.UserRepository;
import com.example.epicbid.global.exception.CustomException;
import com.example.epicbid.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;

    @Transactional
    public void bid(Long auctionId, Long userId, AuctionDto.BidRequest request) {

        // 경매 및 사용자 조회
        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new CustomException(ErrorCode.AUCTION_NOT_FOUND));

        User newBidder = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 경매 상태 검증
        if (!auction.getStatus().name().equals("IN_PROGRESS")) {
            throw new CustomException(ErrorCode.INVALID_AUCTION);
        }

        // 새로운 입찰자의 포인트 차감
        newBidder.deductPoints(request.bidPrice());

        // 기존 최고가 입찰자가 있다면 포인트 환불
        if (auction.getTopBidder() != null) {
            User previousBidder = auction.getTopBidder();
            previousBidder.addPoints(auction.getTopBidPrice());
            log.info("기존 입찰자 {}에게 {}포인트 환불 완료", previousBidder.getId(), auction.getTopBidPrice());

        }

        // 경매 최고가 및 최고 입찰자 갱신
        auction.updateTopBid(request.bidPrice(), newBidder);

    }
}
