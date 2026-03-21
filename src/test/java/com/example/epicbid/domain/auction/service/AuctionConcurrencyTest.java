package com.example.epicbid.domain.auction.service;


import com.example.epicbid.domain.auction.dto.AuctionDto;
import com.example.epicbid.domain.auction.entity.Auction;
import com.example.epicbid.domain.auction.repository.AuctionRepository;
import com.example.epicbid.domain.product.entity.Book;
import com.example.epicbid.domain.product.entity.Product;
import com.example.epicbid.domain.product.enums.BookCondition;
import com.example.epicbid.domain.product.enums.SaleType;
import com.example.epicbid.domain.product.repository.BookRepository;
import com.example.epicbid.domain.product.repository.ProductRepository;
import com.example.epicbid.domain.user.entity.User;
import com.example.epicbid.domain.user.repository.UserRepository;
import com.example.epicbid.global.exception.CustomException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
public class AuctionConcurrencyTest {

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BookRepository bookRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private AuctionRepository auctionRepository;

    private Auction testAuction;
    private List<User> testUsers = new ArrayList<>();

    @BeforeEach
    void setUp() {
        // 판매자 생성
        User seller = userRepository.save(User.builder().email("seller@test.com").password("1234").build());
        // 도서 및 상품 생성
        Book book = bookRepository.save(Book.builder().isbn("123456789").title("테스트 북").author("저자").publisher("출판사").build());
        Product product = productRepository.save(Product.builder().book(book).seller(seller).saleType(SaleType.AUCTION).conditionGrade(BookCondition.S).stock(1).build());

        // 경매 생성 (시작가 10,000원)
        testAuction = auctionRepository.save(Auction.builder()
                .product(product).startPrice(BigDecimal.valueOf(10000)).minPrice(BigDecimal.valueOf(5000)).endTime(LocalDateTime.now().plusDays(1)).build());

        // 입찰할 유서 100명 생성 및 50,000 포인트 지급
        for (int i = 0; i < 100; i++) {
            User user = User.builder().email("user" + i + "@test.com").password("1234").build();
            user.addPoints(BigDecimal.valueOf(50000));
            testUsers.add(userRepository.save(user));
        }
    }

    @Test
    @DisplayName("100명이 동시에 입찰하면 낙관적 락이 발생하여 1명만 성공해야 한다.")
    void concurrencyBidTest() throws InterruptedException {
        int threadCount = 100;
        ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor();
        CountDownLatch latch = new CountDownLatch(threadCount); // 100개의 스레드가 끝날 때까지 기다리는 장치

        AtomicInteger successCount = new AtomicInteger();
        AtomicInteger failCount = new AtomicInteger();

        // 100명이 동시에 20,000원으로 입찰 시도
        BigDecimal bidPrice = BigDecimal.valueOf(20000);

        for (int i = 0; i < threadCount; i++) {
            final User bidder = testUsers.get(i);
            executorService.submit(() -> {
                try {
                    auctionService.bid(testAuction.getId(), bidder.getId(), new AuctionDto.BidRequest(bidPrice));
                    successCount.incrementAndGet(); // 성공 횟수 증가
                } catch (ObjectOptimisticLockingFailureException e) {
                    failCount.incrementAndGet(); // 낙관적 락 충돌로 인한 실패 횟수 증가
                } catch (CustomException e) {
                    failCount.incrementAndGet(); // 경매 입찰 방어 로직으로 인한 실패 횟수 증가
                } catch (Exception e) {
                    System.out.println("기타 에러: " + e.getMessage());
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(); // 모든 스레드가 작업을 마칠 때까지 대기

        // 검증 (Assertion)
        Auction updatedAuction = auctionRepository.findById(testAuction.getId()).orElseThrow();

        System.out.println("성공한 입찰 수: " + successCount.get());
        System.out.println("낙관적 락으로 튕겨난 입찰 수: " + failCount.get());
        System.out.println("현재 경매 버전 (Version): " + updatedAuction.getVersion());

        // 100명이 동시에 요청했지만, 단 1명만 성공해야 함
        assertThat(successCount.get()).isEqualTo(1);
        assertThat(failCount.get()).isEqualTo(99);
        // 버전은 최초 0에서 1번만 업데이트 되었으므로 1이어야 함
        assertThat(updatedAuction.getVersion()).isEqualTo(1);
    }

}
