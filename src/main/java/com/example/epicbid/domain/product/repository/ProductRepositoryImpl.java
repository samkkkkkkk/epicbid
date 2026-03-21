package com.example.epicbid.domain.product.repository;

import com.example.epicbid.domain.product.dto.ProductDto;
import com.example.epicbid.domain.product.entity.Product;
import com.example.epicbid.domain.product.enums.BookCondition;
import com.example.epicbid.domain.product.enums.SaleType;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

import static com.example.epicbid.domain.product.entity.QBook.book;
import static com.example.epicbid.domain.product.entity.QProduct.product;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<Product> searchProducts(ProductDto.SearchCondition condition, Pageable pageable) {
        // 실제 데이터를 가져오는 쿼리
        List<Product> content = queryFactory
                .selectFrom(product)
                .join(product.book, book).fetchJoin()
                .where(
                        saleTypeEq(condition.saleType()),
                        conditionGradeEq(condition.conditionGrade()),
                        keywordContains(condition.keyword())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.id.desc())
                .fetch();

        // 전체 데이터 개수를 가져오는 카운트 쿼리
        JPAQuery<Long> countQuery = queryFactory
                .select(product.count())
                .from(product)
                .join(product.book, book)
                .where(
                        saleTypeEq(condition.saleType()),
                        conditionGradeEq(condition.conditionGrade()),
                        keywordContains(condition.keyword())
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // WHERE 절 헬퍼 메서드
    private BooleanExpression saleTypeEq(SaleType saleType) {
        return saleType != null ? product.saleType.eq(saleType) : null;
    }

    private BooleanExpression conditionGradeEq(BookCondition conditionGrade) {
        return conditionGrade != null ? product.conditionGrade.eq(conditionGrade) : null;
    }

    private BooleanExpression keywordContains(String keyword) {
        if (!StringUtils.hasText(keyword)) {
            return null;
        }

        return book.title.containsIgnoreCase(keyword)
                .or(book.author.containsIgnoreCase(keyword))
                .or(book.publisher.containsIgnoreCase(keyword));
    }
}
