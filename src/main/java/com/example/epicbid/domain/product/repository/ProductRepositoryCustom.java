package com.example.epicbid.domain.product.repository;

import com.example.epicbid.domain.product.dto.ProductDto;
import com.example.epicbid.domain.product.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<Product> searchProducts(ProductDto.SearchCondition condition, Pageable pageable);
}
