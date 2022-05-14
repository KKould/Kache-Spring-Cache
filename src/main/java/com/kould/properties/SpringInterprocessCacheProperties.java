package com.kould.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kache.interprocess-cache")
public class SpringInterprocessCacheProperties extends InterprocessCacheProperties{
}
