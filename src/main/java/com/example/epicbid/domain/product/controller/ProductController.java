package com.example.epicbid.domain.product.controller;

import com.example.epicbid.domain.product.dto.ProductDto;
import com.example.epicbid.domain.product.entity.Product;
import com.example.epicbid.domain.product.service.ProductService;
import com.example.epicbid.global.security.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        return ResponseEntity.status(HttpStatus.CREATED).body(product.getId());
    }

}
