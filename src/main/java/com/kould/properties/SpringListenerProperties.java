package com.kould.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kache.listener")
public class SpringListenerProperties extends ListenerProperties{
}
