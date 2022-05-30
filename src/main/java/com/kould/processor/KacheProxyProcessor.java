package com.kould.processor;

import com.kould.annotation.CacheEntity;
import com.kould.api.Kache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.*;
import java.util.stream.Collectors;

public class KacheProxyProcessor implements ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean {

    private static final String INTERFACE_NOT_FOUND_EXCEPTION = "Kache: This bean's interfaces haven't @CacheEntity?";

    private static final String ANNOTATION_ENTITY_CLASS_NULL_EXCEPTION = "Kache: @CacheEntity.value is null!!!";

    private final Kache kache;

    private Set<String> nameSet;

    private ApplicationContext context;

    public KacheProxyProcessor(Kache kache) {
        this.kache = kache;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
        nameSet = Arrays.stream(applicationContext.getBeanNamesForAnnotation(CacheEntity.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (nameSet.contains(beanName)) {
            Class<?> beanType = ((FactoryBean<?>) context.getBean("&" + beanName)).getObjectType();
            assert beanType != null;
            Class<?> entityClass = getEntityClassForClass(beanType);
            if (entityClass == null) {
                throw new KacheBeansException(ANNOTATION_ENTITY_CLASS_NULL_EXCEPTION);
            }
            return kache.getProxy(bean, entityClass);
        }
        return bean;
    }

    @Override
    public void destroy() throws Exception {
        kache.destroy();
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        kache.init();
    }

    class KacheBeansException extends BeansException {
        public KacheBeansException(String msg) {
            super(msg);
        }
    }

    /**
     * 通过Mapper的Class进行@CacheEntity的判定并获取其中的Entity类型
     * 若无数据则抛出KacheBeanException
     * @param clazz Mapper的Class
     * @return Mapper对应的
     */
    private Class<?> getEntityClassForClass(Class<?> clazz) throws KacheBeansException{
        if (clazz != null && clazz.isAnnotationPresent(CacheEntity.class)) {
            return clazz.getDeclaredAnnotation(CacheEntity.class).value();
        }
        throw new KacheBeansException(INTERFACE_NOT_FOUND_EXCEPTION);
    }
}
