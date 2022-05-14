package com.kould.config;

import com.kould.processor.KacheProxyProcessor;
import com.kould.properties.SpringDaoProperties;
import com.kould.properties.SpringDataFieldProperties;
import com.kould.properties.SpringInterprocessCacheProperties;
import com.kould.properties.SpringListenerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({
        SpringDaoProperties.class,
        SpringDataFieldProperties.class,
        SpringInterprocessCacheProperties.class,
        SpringListenerProperties.class
})
public class KacheSpringConfig {

    @Bean
    public KacheProxyProcessor kacheProxyProcessor() {
        return new KacheProxyProcessor();
    }
}
