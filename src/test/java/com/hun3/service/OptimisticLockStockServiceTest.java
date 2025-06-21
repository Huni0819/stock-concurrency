package com.hun3.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.hun3.domain.Stock;
import com.hun3.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@Slf4j
@SpringBootTest
public class OptimisticLockStockServiceTest {

    @Autowired
    private OptimisticLockStockService stockService;

    @Autowired
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        stockRepository.saveAndFlush(new Stock(1L, 100L));
    }

    @AfterEach
    void tearDown() {
        stockRepository.deleteAll();
    }

    @Test
    public void update_failed() throws InterruptedException {
        final Long productId = 1L;
        final Long decreaseAmount = 1L;
        final int threadCount = 2;

        ExecutorService executorService = Executors.newFixedThreadPool(32);
        CountDownLatch latch = new CountDownLatch(2);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
               try {
                   stockService.decrease(productId, decreaseAmount);
               } catch(RuntimeException e) {
                   log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
               } finally {
                   latch.countDown();
               }
            });
        }

        latch.await();
        Stock stock = stockRepository.findByProductId(productId);
        assertEquals(98, stock.getQuantity());
    }

}
