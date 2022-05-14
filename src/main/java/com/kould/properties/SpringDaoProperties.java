package com.kould.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("kache.dao")
public class SpringDaoProperties extends DaoProperties{
    private String mapperPackage = "";

    public String getMapperPackage() {
        return mapperPackage;
    }

    public void setMapperPackage(String mapperPackage) {
        this.mapperPackage = mapperPackage;
    }
}
