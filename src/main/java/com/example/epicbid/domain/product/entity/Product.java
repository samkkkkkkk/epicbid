package com.example.epicbid.domain.product.entity;

import com.example.epicbid.domain.product.enums.BookCondition;
import com.example.epicbid.domain.product.enums.SaleType;
import com.example.epicbid.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 어떤 책인가?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    // 누가 파는가? (플랫폼 직영이면 null 허용하거나 Admin User ID)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SaleType saleType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookCondition conditionGrade;

    // 일반 판매일 경우의 고정가 (경매일 경우 시작가로 사용하거나 null)
    @Column(precision = 15, scale = 2)
    private BigDecimal price;

    // 재고량 (경매나 단일 중고도서는 1)
    @Column(nullable = false)
    private Integer stock;

    @Builder
    public Product(Book book, User seller, SaleType saleType, BookCondition conditionGrade, BigDecimal price, Integer stock) {
        this.book = book;
        this.seller = seller;
        this.saleType = saleType;
        this.conditionGrade = conditionGrade;
        this.price = price;
        this.stock = stock;
    }

    public static Product createNormalProduct(Book book, User seller, BookCondition conditionGrade, BigDecimal price, Integer stock) {
        return Product.builder()
                .book(book)
                .seller(seller)
                .saleType(SaleType.NORMAL)
                .conditionGrade(conditionGrade)
                .price(price)
                .stock(stock)
                .build();
    }

    public static Product createAuctionProduct(Book book, User seller, BookCondition conditionGrade) {
        return Product.builder()
                .book(book)
                .seller(seller)
                .saleType(SaleType.AUCTION)
                .conditionGrade(conditionGrade)
                .stock(1)
                .build();
    }

}
