package com.hun3.repository;

import com.hun3.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StockRepository extends JpaRepository<Stock, Long> {

    Stock findByProductId(Long productId);
}
