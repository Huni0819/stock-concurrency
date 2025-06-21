package com.hun3.facade;

import com.hun3.service.OptimisticLockStockService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OptimisticLockStockFacade {
    private final OptimisticLockStockService optimisticLockStockService;

    public OptimisticLockStockFacade(OptimisticLockStockService optimisticLockStockService) {
        this.optimisticLockStockService = optimisticLockStockService;
    }

    public void decrease(Long id, Long quantity) throws InterruptedException {
        while(true) {
            try {
                optimisticLockStockService.decrease(id, quantity);
                break;
            } catch (ObjectOptimisticLockingFailureException e) {
                log.error("{} 발생, 업데이트 실패", e.getClass().getSimpleName(), e);
                Thread.sleep(50);
            }
        }
    }
}
