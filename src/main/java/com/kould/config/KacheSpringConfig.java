package com.kould.config;

import com.kould.api.Kache;
import com.kould.codec.KryoRedisCodec;
import com.kould.core.CacheHandler;
import com.kould.core.impl.BaseCacheHandler;
import com.kould.encoder.CacheEncoder;
import com.kould.encoder.impl.BaseCacheEncoder;
import com.kould.lock.KacheLock;
import com.kould.lock.impl.LocalLock;
import com.kould.manager.IBaseCacheManager;
import com.kould.manager.InterprocessCacheManager;
import com.kould.manager.RemoteCacheManager;
import com.kould.manager.impl.BaseCacheManagerImpl;
import com.kould.manager.impl.GuavaCacheManager;
import com.kould.manager.impl.RedisCacheManager;
import com.kould.processor.KacheProxyProcessor;
import com.kould.properties.*;
import com.kould.service.RedisService;
import com.kould.strategy.Strategy;
import com.kould.strategy.impl.DBFirst;
import com.kould.type.KacheSpringBuilder;
import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisURI;
import io.lettuce.core.codec.RedisCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;
import java.time.temporal.ChronoUnit;

@Configuration
@EnableConfigurationProperties({
        SpringDaoProperties.class,
        SpringDataFieldProperties.class,
        SpringInterprocessCacheProperties.class,
        SpringListenerProperties.class,
        SpringKeyProperties.class
})
public class KacheSpringConfig {

    @Autowired
    private CacheEncoder cacheEncoder;

    @Autowired
    private KacheLock kacheLock;

    @Autowired
    private SpringDaoProperties daoProperties;

    @Autowired
    private SpringDataFieldProperties dataFieldProperties;

    @Autowired
    private SpringInterprocessCacheProperties interprocessCacheProperties;


    @Bean
    @ConditionalOnMissingBean
    public KacheProxyProcessor kacheProxyProcessor() {
        return new KacheProxyProcessor();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisClient redisClient() {
        return RedisClient.create(RedisURI.builder()
                .withHost("localhost")
                .withPort(6379)
                .withTimeout(Duration.of(10, ChronoUnit.SECONDS))
                .build());
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheEncoder cacheEncoder() {
        return BaseCacheEncoder.getInstance();
    }

    @Bean
    @ConditionalOnMissingBean
    public KacheLock kacheLock() {
        return new LocalLock();
    }

    @Bean
    @ConditionalOnMissingBean
    public CacheHandler cacheHandler() {
        return new BaseCacheHandler();
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisCodec<String, Object> redisCodec() {
        return new KryoRedisCodec();
    }

    @Bean
    @ConditionalOnMissingBean
    public InterprocessCacheManager interprocessCacheManager() {
        return new GuavaCacheManager(daoProperties, interprocessCacheProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public RedisService redisService(RedisClient redisClient, RedisCodec<String,Object> redisCodec) {
        return new RedisService(daoProperties, redisClient, redisCodec);
    }

    @Bean
    @ConditionalOnMissingBean
    public RemoteCacheManager remoteCacheManager(RedisService redisService) {
        return new RedisCacheManager(dataFieldProperties, daoProperties
                , redisService, kacheLock, cacheEncoder);
    }

    @Bean
    @ConditionalOnMissingBean
    public IBaseCacheManager iBaseCacheManager(InterprocessCacheManager interprocessCacheManager, RemoteCacheManager remoteCacheManager) {
        return new BaseCacheManagerImpl(interprocessCacheManager
                , remoteCacheManager, interprocessCacheProperties,kacheLock,cacheEncoder,dataFieldProperties);
    }

    @Bean
    @ConditionalOnMissingBean
    public Strategy strategy(IBaseCacheManager iBaseCacheManager) {
        return new DBFirst(iBaseCacheManager);
    }

    @Bean
    @ConditionalOnMissingBean
    public Kache kache() {
        return new KacheSpringBuilder().build();
    }
}
