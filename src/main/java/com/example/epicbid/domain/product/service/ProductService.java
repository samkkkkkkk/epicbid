package com.example.epicbid.domain.product.service;

import com.example.epicbid.domain.product.dto.ProductDto;
import com.example.epicbid.domain.product.entity.Book;
import com.example.epicbid.domain.product.entity.Product;
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
        Book book = getOrCreateBook(request);

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

    private Book getOrCreateBook(ProductDto.AdminRegisterRequest request) {
        return bookRepository.findByIsbn(request.isbn())
                .orElseGet(() -> bookRepository.save(
                        Book.of(request.isbn(), request.title(), request.author(), request.publisher())
                ));
    }


    @Transactional(readOnly = true)
    public Page<ProductDto.ListResponse> getProductList(ProductDto.SearchCondition condition, Pageable pageable) {
        Page<Product> products = productRepository.searchProducts(condition, pageable);

        return products.map(ProductDto.ListResponse::from);
    }
}
