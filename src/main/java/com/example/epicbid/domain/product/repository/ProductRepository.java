package com.example.epicbid.domain.product.repository;

import com.example.epicbid.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    @Query("SELECT p FROM Product p " +
           "JOIN FETCH p.book " +
           "JOIN FETCH p.seller " +
           "WHERE p.id = :productId")
    Optional<Product> findByIdWithBookAndSeller(@Param("productId") Long productId);
}
