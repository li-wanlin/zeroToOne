package com.jnl.manage;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;
import java.util.concurrent.ConcurrentHashMap;

//@Component
public class MatlabEngineManager {

    private final ConcurrentHashMap<String, EngineInfo<?>> engines = new ConcurrentHashMap<>();
    private final Object lock = new Object();
    private static final long INACTIVITY_TIMEOUT = 24 * 60 * 60 * 1000; // 24小时


    private static final Logger logger = LoggerFactory.getLogger(MatlabEngineManager.class);

    /**
     * 存储引擎信息（包括实例和创建时间）
     */
    public static class EngineInfo<T> {
        private final T engine;
        private final long creationTime;
        private long lastAccessTime;

        public EngineInfo(T engine) {
            this.engine = engine;
            this.creationTime = System.currentTimeMillis();
            this.lastAccessTime = this.creationTime;
        }

        public T getEngine() {
            this.lastAccessTime = System.currentTimeMillis();
            return engine;
        }

        public long getInactivityTime() {
            return System.currentTimeMillis() - lastAccessTime;
        }
    }

    /**
     * 获取引擎（使用Class1作为通用类型）
     */
    @SuppressWarnings("unchecked")
    public <T> T getEngine(String jarName) {
        return (T) engines.computeIfAbsent(jarName, key -> {
            synchronized (lock) {
                try {
                    // 使用自定义类加载器加载特定JAR中的Class1类
                    ClassLoader classLoader = getClassLoaderForJar(jarName);

                    // 根据JAR包名确定类名前缀（假设skw2.jar对应skw2包）
                    String packageName = jarName.substring(0, jarName.lastIndexOf('.'));
                    String className = packageName + ".Class1";

                    // 加载类
                    Class<?> engineClass = classLoader.loadClass(className);

                    // 创建实例
                    Object instance = engineClass.getDeclaredConstructor().newInstance();
                    return new EngineInfo<>(instance);
                } catch (Exception e) {
                    throw new RuntimeException("初始化MATLAB引擎失败: " + jarName, e);
                }
            }
        }).getEngine();
    }

    /**
     * 为特定JAR创建类加载器
     */
    public ClassLoader getClassLoaderForJar(String jarName) {
        try {
            // 实际项目中需要根据JAR路径创建URLClassLoader
            java.net.URL jarUrl = new java.net.URL("file:" + "/path/to/" + jarName);
            return new java.net.URLClassLoader(new java.net.URL[]{jarUrl},
                    Thread.currentThread().getContextClassLoader());
        } catch (Exception e) {
            // 默认使用当前类加载器（不推荐在生产环境使用）
            return Thread.currentThread().getContextClassLoader();
        }
    }

    /**
     * 释放指定引擎
     */
    public void releaseEngine(String jarName) {
        EngineInfo<?> info = engines.remove(jarName);
        if (info != null) {
            try {
                if (info.getEngine() instanceof AutoCloseable) {
                    ((AutoCloseable) info.getEngine()).close();

                    logger.info("MATLAB引擎 [{}] 已释放",jarName);

                    //System.out.println("MATLAB引擎 [" + jarName + "] 已释放");
                }
            } catch (Exception e) {
                logger.error("释放MATLAB引擎失败: {}",jarName,e);
                //System.err.println("释放MATLAB引擎失败: " + jarName);
            }
        }
    }

    /**
     * 释放所有超过24小时未使用的引擎
     */
    public void releaseInactiveEngines() {
        engines.entrySet().removeIf(entry -> {
            EngineInfo<?> info = entry.getValue();
            if (info.getInactivityTime() >= INACTIVITY_TIMEOUT) {
                releaseEngine(entry.getKey());

                logger.info("MATLAB引擎 [{}] 因24小时未使用被释放",entry.getKey());
                //System.out.println("MATLAB引擎 [" + entry.getKey() + "] 因24小时未使用被释放");
                return true;
            }
            return false;
        });
    }

    /**
     * 释放所有引擎
     */
    public void releaseAll() {
        engines.keySet().forEach(this::releaseEngine);
    }

    @PreDestroy
    public void cleanup() {
        logger.info("开始关闭所有MATLAB引擎");
        releaseAll();
    }
}