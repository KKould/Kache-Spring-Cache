package com.kould.processor;

import cn.hutool.core.util.StrUtil;
import com.kould.annotation.CacheEntity;
import com.kould.api.Kache;
import com.kould.properties.SpringDaoProperties;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class KacheProxyProcessor implements ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean {

    private static final String MAPPER_PACKAGE_PATH_EXCEPTION = "Kache: Mapper's package path is wrong!!!";

    private static final String ANNOTATION_ENTITY_CLASS_NULL_EXCEPTION = "Kache: @CacheEntity.value is null!!!";

    @Autowired
    private SpringDaoProperties daoProperties;

    @Autowired
    private Kache kache;

    private String methodPackage;

    private Set<String> mapperNames;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        mapperNames = new HashSet<>(Arrays.asList(applicationContext.getBeanNamesForAnnotation(CacheEntity.class)));
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (mapperNames.contains(beanName)) {
            try {
                Class<?> interfaceClass = Class.forName(
                        methodPackage + "." + StrUtil.upperFirst(beanName));
                Class<?> entityClass = interfaceClass.getDeclaredAnnotation(CacheEntity.class).value();
                if (entityClass == null) {
                    throw new KacheBeansException(ANNOTATION_ENTITY_CLASS_NULL_EXCEPTION);
                }
                return kache.getProxy(bean, entityClass);
            } catch (ClassNotFoundException e) {
                throw new KacheBeansException(MAPPER_PACKAGE_PATH_EXCEPTION);
            }
        }
        return bean;
    }

    @Override
    public void destroy() throws Exception {
        kache.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (methodPackage == null) {
            methodPackage = daoProperties.getMapperPackage();
        }
        kache.init();
    }

    class KacheBeansException extends BeansException {
        public KacheBeansException(String msg) {
            super(msg);
        }
    }

    public void setMethodPackage(String methodPackage) {
        this.methodPackage = methodPackage;
    }
}
