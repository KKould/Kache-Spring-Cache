package com.kould.type;

import com.kould.api.Kache;
import com.kould.core.CacheHandler;
import com.kould.encoder.CacheEncoder;
import com.kould.manager.IBaseCacheManager;
import com.kould.manager.InterprocessCacheManager;
import com.kould.manager.RemoteCacheManager;
import com.kould.properties.*;
import com.kould.service.RedisService;
import com.kould.strategy.Strategy;
import io.lettuce.core.RedisClient;
import io.lettuce.core.codec.RedisCodec;
import org.springframework.beans.factory.annotation.Autowired;

public class KacheSpringBuilder extends Kache.Builder {

    @Autowired
    private RedisClient redisClient;

    @Autowired
    private CacheEncoder cacheEncoder;

    @Autowired
    private CacheHandler cacheHandler;

    @Autowired
    private RedisCodec<String, Object> redisCodec;

    @Autowired
    private SpringDaoProperties daoProperties;

    @Autowired
    private SpringDataFieldProperties dataFieldProperties;

    @Autowired
    private SpringInterprocessCacheProperties interprocessCacheProperties;

    @Autowired
    private SpringListenerProperties listenerProperties;

    @Autowired
    private SpringKeyProperties keyProperties;

    @Autowired
    private InterprocessCacheManager interprocessCacheManager;

    @Autowired
    private RedisService redisService;

    @Autowired
    private RemoteCacheManager remoteCacheManager;

    @Autowired
    private IBaseCacheManager iBaseCacheManager;

    @Autowired
    private Strategy strategy;

    @Override
    public Kache build() {
        return new Kache(this);
    }
}
