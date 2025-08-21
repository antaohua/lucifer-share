package com.muniu.cloud.lucifer.share.service.utils;


import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author antaohua
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    @Getter
    private static ApplicationContext context;

    private static final AtomicBoolean isInit = new AtomicBoolean(false);

    private static void init(ApplicationContext context) {
        if (isInit.compareAndSet(false, true)) {
            SpringContextUtils.context = context;
        }
    }

    public static boolean isInit() {
        return isInit.get();
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static <T> T getBean(String name, Class<T> clazz) {
        return context.getBean(name, clazz);
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }

    public static boolean containsBean(String name) {
        return context.containsBean(name);
    }


    public static boolean isSingleton(String name) {
        return context.isSingleton(name);
    }

    public static Class<?> getType(String name) {
        return context.getType(name);
    }


    public static String[] getAliases(String name) {
        return context.getAliases(name);
    }


    public static String getVal(String name) {
        return context.getEnvironment().getProperty(name);
    }


    @Override
    public void setApplicationContext(@NotNull ApplicationContext applicationContext) throws BeansException {
        init(applicationContext);
    }
}
