package com.kould.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kache.dao")
public class SpringDaoProperties extends DaoProperties{
}
