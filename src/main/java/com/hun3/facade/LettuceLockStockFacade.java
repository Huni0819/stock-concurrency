package com.hun3.facade;

import com.hun3.repository.RedisLockRepository;
import com.hun3.service.StockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LettuceLockStockFacade {

    private final RedisLockRepository repository;
    private final StockService stockService;

    public LettuceLockStockFacade(RedisLockRepository repository, StockService stockService) {
        this.repository = repository;
        this.stockService = stockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {

        while(!repository.lock(id)) {
            log.info("lock 획득 실패");
            Thread.sleep(100);
        }

        log.info("lock 획득 성공");
        try {
            stockService.decrease(id, quantity);
        } finally {
            repository.unLock(id);
            log.info("lock 해제");
        }
    }
}
