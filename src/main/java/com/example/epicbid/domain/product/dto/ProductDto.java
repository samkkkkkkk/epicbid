package com.example.epicbid.domain.product.dto;

import com.example.epicbid.domain.product.entity.Product;
import com.example.epicbid.domain.product.enums.BookCondition;
import com.example.epicbid.domain.product.enums.SaleType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProductDto {

    public record AdminRegisterRequest(
            @NotBlank(message = "ISBN은 필수입니다.")
            String isbn,

            @NotBlank(message = "책 제목은 필수입니다.")
            String title,

            @NotBlank(message = "저자는 필수입니다.")
            String author,

            @NotBlank(message = "출판사는 필수입니다.")
            String publisher,

            @NotNull(message = "도서 상태를 선택해주세요.")
            BookCondition conditionGrade,

            @NotNull(message = "판매 가격을 입력해주세요.")
            BigDecimal price,

            @NotNull(message = "재고 수량을 입력해주세요.")
            @Min(value = 1, message = "재고는 최소 1개 이상이여야 합니다.")
            Integer stock
    ){}

    public record AdminRegisterResponse(
            Long productId,
            String isbn,
            String title,
            BigDecimal price,
            Integer stock
    ){
        public static AdminRegisterResponse from(Product product) {
            return new AdminRegisterResponse(
                    product.getId(),
                    product.getBook().getIsbn(),
                    product.getBook().getTitle(),
                    product.getPrice(),
                    product.getStock()
            );
        }
    }

    public record SearchCondition(
            SaleType saleType,
            BookCondition conditionGrade,
            String keyword
    ) {}

    public record ListResponse(
            Long productId,
            String isbn,
            String title,
            String author,
            String publisher,
            SaleType saleType,
            BigDecimal price, // (경매는 null일 수 있음)
            BookCondition conditionGrade
    ) {
        public static ListResponse from(Product product) {
            return new ListResponse(
                    product.getId(),
                    product.getBook().getIsbn(),
                    product.getBook().getTitle(),
                    product.getBook().getAuthor(),
                    product.getBook().getPublisher(),
                    product.getSaleType(),
                    product.getPrice(),
                    product.getConditionGrade()
            );
        }
    }
}
