package com.example.epicbid.domain.auction.entity;

import ch.qos.logback.core.spi.ErrorCodes;
import com.example.epicbid.domain.product.entity.Product;
import com.example.epicbid.domain.product.enums.AuctionStatus;
import com.example.epicbid.domain.user.entity.User;
import com.example.epicbid.global.exception.CustomException;
import com.example.epicbid.global.exception.ErrorCode;
import com.example.epicbid.global.exception.ErrorResponse;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "auctions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Auction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 대상 상품 (1:1 매핑)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal startPrice;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal minPrice; // 타임드랍 하한선

    // 타임드랍을 통해 현재 깎여서 내려온 '즉시 구매가'
    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal currentBuyPrice;

    // 누군가 입찰한 최고 금액 (없으면 null)
    @Column(precision = 15, scale = 2)
    private BigDecimal topBidPrice;

    // 현재 1등 입찰자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "top_bidder_id")
    private User topBidder;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AuctionStatus status = AuctionStatus.PENDING;

    @Column(nullable = false)
    private LocalDateTime endTime;

    // 낙관적 락(Optimistic Lock)을 위한 버전 관리 컬럼
    @Version
    private Long version;

    @Builder
    public Auction(Product product, BigDecimal startPrice, BigDecimal minPrice, LocalDateTime endTime) {
        this.product = product;
        this.startPrice = startPrice;
        this.minPrice = minPrice;
        this.currentBuyPrice = startPrice; // 초기 즉시 구매가는 시작가와 동일
        this.endTime = endTime;
        this.status = AuctionStatus.IN_PROGRESS;
    }

    // 입찰 비즈니스 로직
    public void updateTopBid(BigDecimal newBidPrice, User newBidder) {
        BigDecimal targetPrice = topBidPrice != null ? topBidPrice : startPrice;
        if (newBidPrice.compareTo(targetPrice) <= 0) {
            throw new CustomException(ErrorCode.AUCTION_INVALID_INPUT);
        }

        this.topBidPrice = newBidPrice;
        this.topBidder = newBidder;
    }
}
