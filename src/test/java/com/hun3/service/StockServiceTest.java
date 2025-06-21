package com.hun3.service;

import static org.junit.jupiter.api.Assertions.*;

import com.hun3.domain.Stock;
import com.hun3.repository.StockRepository;
import com.hun3.service.StockService;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class StockServiceTest {
    @Autowired
    private StockService stockService;

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
    void decrease_stock_one_try() {

        stockService.decrease(1L, 1L);

        Stock stock = stockRepository.findByProductId(1L);
        assertEquals(99, stock.getQuantity());
    }

    @Test
    void decrease_stock_100_times_concurrently() throws InterruptedException {

        int threadCount = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; i++) {
            executorService.submit(() -> {
               try {
                   stockService.decrease(1L, 1L);
               } finally {
                   latch.countDown();
               }
            });
        }

        latch.await();

        Stock stock = stockRepository.findByProductId(1L);
        assertEquals(0, stock.getQuantity());
    }
}