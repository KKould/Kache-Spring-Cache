package com.kould.type;

import com.kould.api.Kache;
import com.kould.properties.*;

public class KacheSpringBuilder extends Kache.Builder {

    private final SpringDaoProperties daoProperties;

    private final SpringDataFieldProperties dataFieldProperties;

    private final SpringInterprocessCacheProperties interprocessCacheProperties;

    private final SpringListenerProperties listenerProperties;

    private final SpringKeyProperties keyProperties;

    public KacheSpringBuilder(SpringDaoProperties daoProperties, SpringDataFieldProperties dataFieldProperties, SpringInterprocessCacheProperties interprocessCacheProperties, SpringListenerProperties listenerProperties, SpringKeyProperties keyProperties) {
        this.daoProperties = daoProperties;
        this.dataFieldProperties = dataFieldProperties;
        this.interprocessCacheProperties = interprocessCacheProperties;
        this.listenerProperties = listenerProperties;
        this.keyProperties = keyProperties;
    }

    @Override
    public Kache build() {
        return Kache.builder()
                .load(DaoProperties.class, daoProperties)
                .load(DataFieldProperties.class, dataFieldProperties)
                .load(InterprocessCacheProperties.class, interprocessCacheProperties)
                .load(ListenerProperties.class, listenerProperties)
                .load(KeyProperties.class, keyProperties)
                .build();
    }
}
