package com.kould.processor;

import com.kould.annotation.CacheEntity;
import com.kould.api.Kache;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public class KacheProxyProcessor implements ApplicationContextAware, BeanPostProcessor, InitializingBean, DisposableBean {

    private static final String INTERFACE_NOT_FOUND_EXCEPTION = "Kache: This bean's interfaces haven't @CacheEntity?";

    private static final String ANNOTATION_ENTITY_CLASS_NULL_EXCEPTION = "Kache: @CacheEntity.value is null!!!";

    @Autowired
    private Kache kache;

    private Set<String> nameSet;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        nameSet = Arrays.stream(applicationContext.getBeanNamesForAnnotation(CacheEntity.class))
                .collect(Collectors.toSet());
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (nameSet.contains(beanName)) {
            try {
                Class<?> interfaceClass = getTargetAnnotation(bean.getClass().getInterfaces());
                Class<?> entityClass = interfaceClass.getDeclaredAnnotation(CacheEntity.class).value();
                if (entityClass == null) {
                    throw new KacheBeansException(ANNOTATION_ENTITY_CLASS_NULL_EXCEPTION);
                }
                return kache.getProxy(bean, entityClass);
            } catch (KacheBeansException e) {
                e.printStackTrace();
                throw e;
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
        kache.init();
    }

    class KacheBeansException extends BeansException {
        public KacheBeansException(String msg) {
            super(msg);
        }
    }

    private Class<?> getTargetAnnotation(Class<?>[] interFaces) {
        for (Class<?> interFace : interFaces) {
            if (interFace.isAnnotationPresent(CacheEntity.class)) {
                return interFace;
            }
        }
        throw new KacheBeansException(INTERFACE_NOT_FOUND_EXCEPTION);
    }
}
