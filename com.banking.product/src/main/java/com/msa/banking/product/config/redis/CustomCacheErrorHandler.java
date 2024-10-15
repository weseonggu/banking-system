package com.msa.banking.product.config.redis;

import com.msa.banking.product.presentation.exception.custom.TryAgainException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class CustomCacheErrorHandler implements CacheErrorHandler {

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        // 캐시 조회 오류 처리
        log.warn("Cache get error: " + exception.getMessage());
        throw new TryAgainException("나중에 다시 시도해 주세요");
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        // 캐시 저장 오류 처리
        log.warn("Cache put error: " + exception.getMessage());
        throw new TryAgainException("나중에 다시 시도해 주세요");
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        // 캐시 삭제 오류 처리
        log.warn("Cache evict error: " + exception.getMessage());
        throw new TryAgainException("나중에 다시 시도해 주세요");
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        // 캐시 전체 삭제 오류 처리
        log.warn("Cache clear error: " + exception.getMessage());
        throw new TryAgainException("나중에 다시 시도해 주세요");
    }
}
