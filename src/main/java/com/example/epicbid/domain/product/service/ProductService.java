package com.example.epicbid.domain.product.service;

import com.example.epicbid.domain.product.dto.ProductDto;
import com.example.epicbid.domain.product.entity.Book;
import com.example.epicbid.domain.product.entity.Product;
import com.example.epicbid.domain.product.enums.BookCondition;
import com.example.epicbid.domain.product.repository.BookRepository;
import com.example.epicbid.domain.product.repository.ProductRepository;
import com.example.epicbid.domain.user.entity.User;
import com.example.epicbid.domain.user.repository.UserRepository;
import com.example.epicbid.global.exception.CustomException;
import com.example.epicbid.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final BookRepository bookRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Transactional
    public Product registerAdminProduct(ProductDto.AdminRegisterRequest request, Long adminId) {

        // 관리자 유저 정보 가져오기
        User adminUser = userRepository.findById(adminId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 도서 정보 저장하기
        Book book = getOrCreateBook(request.isbn(), request.title(), request.author(), request.publisher());

        // 상품 정보 저장
        Product product = Product.createNormalProduct(
                book,
                adminUser,
                request.conditionGrade(),
                request.price(),
                request.stock());

        Product savedProduct = productRepository.save(product);

        return savedProduct;
    }



    @Transactional(readOnly = true)
    public Page<ProductDto.ListResponse> getProductList(ProductDto.SearchCondition condition, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(condition, pageable);

        return products.map(ProductDto.ListResponse::from);
    }

    // 일반 유저 중고 상품 등록 비즈니스 로직
    @Transactional
    public ProductDto.Response registerUsedProduct(ProductDto.UsedRegisteredRequest request, Long sellerId) {
        // 중고 도서는 새 상품(NEW)으로 등록할 수 없음
        if (request.conditionGrade() == BookCondition.NEW) {
            throw new CustomException(ErrorCode.INVALID_CONDITION);
        }

        // 판매자 정보 조회
        User seller = userRepository.findById(sellerId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // 도서 카탈로그 확인 및 생성
        Book book = getOrCreateBook(request.isbn(), request.title(), request.author(), request.publisher());

        // 상품 생성 (재고 1개로 고정)
        Product product = Product.createNormalProduct(
                book,
                seller,
                request.conditionGrade(),
                request.price(),
                1
        );

        Product savedProduct = productRepository.save(product);
        return ProductDto.Response.from(savedProduct);
    }

    // 상품 상세 조회
    @Transactional(readOnly = true)
    public ProductDto.DetailResponse getProductDetail(Long productId) {
        Product product = productRepository.findByIdWithBookAndSeller(productId)
                .orElseThrow(() -> new CustomException(ErrorCode.PRODUCT_NOT_FOUND));

        return ProductDto.DetailResponse.from(product);
    }

    // 경매 상품 데이터 생성
    @Transactional
    public Product createProductForAuction(
            String isbn, String title, String author, String publisher,
            User seller, BookCondition conditionGrade
    ) {
        // 도서 확인 및 생성
        Book book = getOrCreateBook(isbn, title, author, publisher);

        // 경매용 Product 새성
        Product product = Product.createAuctionProduct(book, seller, conditionGrade);

        return productRepository.save(product);
    }

    // 책 등록, 조회
    private Book getOrCreateBook(String isbn, String title, String author, String publisher) {
        return bookRepository.findByIsbn(isbn)
                .orElseGet(() -> bookRepository.save(
                        Book.of(isbn, title, author, publisher)
                ));
    }



}
