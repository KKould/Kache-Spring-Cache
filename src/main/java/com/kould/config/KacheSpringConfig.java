package com.kould.config;

import com.kould.api.Kache;
import com.kould.processor.KacheProxyProcessor;
import com.kould.properties.*;
import com.kould.type.KacheSpringBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SpringDaoProperties.class,
        SpringDataFieldProperties.class,
        SpringInterprocessCacheProperties.class,
        SpringListenerProperties.class,
        SpringKeyProperties.class
})
public class KacheSpringConfig {

    @Bean
    @ConditionalOnMissingBean
    public Kache kache(SpringDaoProperties daoProperties, SpringDataFieldProperties dataFieldProperties
            , SpringInterprocessCacheProperties interprocessCacheProperties,SpringListenerProperties listenerProperties, SpringKeyProperties keyProperties) {
        return new KacheSpringBuilder(daoProperties, dataFieldProperties, interprocessCacheProperties, listenerProperties, keyProperties)
                .build();
    }

    @Bean
    public KacheProxyProcessor kacheProxyProcessor(Kache kache) {
        return new KacheProxyProcessor(kache);
    }
}
