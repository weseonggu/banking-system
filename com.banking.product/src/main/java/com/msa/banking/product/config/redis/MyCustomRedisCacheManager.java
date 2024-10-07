package com.msa.banking.product.config.redis;

import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.core.RedisTemplate;

class MyCustomRedisCacheManager {


//    public MyCustomerRedisCacheManager(RedisTemplate redisTemplate) {
//        super(redisTemplate);
//    }
//
//    @Override
//    public Cache getCache(String name) {
//        return new RedisCacheWrapper(super.getCache(name));
//    }
//
//
//    protected static class RedisCacheWrapper implements Cache {
//
//        private final Cache delegate;
//
//        public RedisCacheWrapper(Cache redisCache) {
//            Assert.notNull(redisCache, "'delegate' must not be null");
//            this.delegate = redisCache;
//        }
//
//        @Override
//        public Cache.ValueWrapper get(Object key) {
//            try {
//                delegate.get(key);
//            }
//            catch (Exception e) {
//                return handleErrors(e);
//            }
//        }
//
//        @Override
//        public void put(Object key, Object value) {
//            try {
//                delegate.put(key, value);
//            }
//            catch (Exception e) {
//                handleErrors(e);
//            }
//        }
//
//        // implement clear(), evict(key), get(key, type), getName(), getNativeCache(), putIfAbsent(key, value) accordingly (delegating to the delegate).
//
//        protected <T> T handleErrors(Exception e) throws Exception {
//            if (e instanceof <some RedisConnection Exception type>) {
//                // log the connection problem
//                return null;
//            }
//            else if (<something different>) { // act appropriately }
//            ...
//            else {
//                    throw e;
//                }
//            }
//        }
    }
