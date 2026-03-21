package com.example.epicbid.domain.product.controller;

import com.example.epicbid.domain.product.dto.ProductDto;
import com.example.epicbid.domain.product.entity.Product;
import com.example.epicbid.domain.product.service.ProductService;
import com.example.epicbid.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // [관리자 전용] 직영 상품 대량 등록
    @PostMapping("/admin")
    public ResponseEntity<?> registerAdminProduct(
            @Valid @RequestBody ProductDto.AdminRegisterRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
            ) {
        Product product = productService.registerAdminProduct(request, userDetails.userId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductDto.AdminRegisterResponse.from(product));
    }

    @GetMapping
    public ResponseEntity<Page<ProductDto.ListResponse>> getProducts(
            @ModelAttribute ProductDto.SearchCondition condition,
            @PageableDefault(size = 20) Pageable pageable
    ) {
        Page<ProductDto.ListResponse> response = productService.getProductList(condition, pageable);
        return ResponseEntity.ok().body(response);
    }

}
