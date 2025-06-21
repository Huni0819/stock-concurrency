package com.hun3.service;

import com.hun3.domain.Stock;
import com.hun3.repository.StockRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PessimisticLockStockService {

    private final StockRepository stockRepository;

    public PessimisticLockStockService(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Transactional
    public void decrease(Long productId, Long quantity) {
        Stock stock = stockRepository.findByProductIdWithPessimisticLock(productId);
        stock.decrease(quantity);
        stockRepository.save(stock);
    }
}
