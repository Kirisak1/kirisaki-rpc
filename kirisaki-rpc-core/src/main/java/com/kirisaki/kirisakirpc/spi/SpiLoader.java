package com.kirisaki.kirisakirpc.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.kirisaki.kirisakirpc.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 通过 SPI 机制允许用户自定义序列化器
 */
@Slf4j
public class SpiLoader {
    /**
     * 存储已加载的类:接口名=>(key => 实现类)
     */
    private static final Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();
    /**
     * 对象实例缓存(避免重复 new), 类路径=>对象实例,单例模式
     */
    private static final Map<String, Object> instanceCache = new ConcurrentHashMap<>();
    /**
     * 系统 SPI 目录
     */
    private static final String RPC_SYSTEM_SPI_DIR = "META-INF/rpc/system/";
    /**
     * 用户自定义 SPI 目录
     */
    private static final String RPC_CUSTOM_SPI_DIR = "META-INF/rpc/custom/";
    /**
     * 扫描路径
     */
    private static final String[] SCAN_DIRS = new String[]{RPC_SYSTEM_SPI_DIR, RPC_CUSTOM_SPI_DIR};
    /**
     * 动态加载的类列表
     */
    private static final List<Class<?>> LOAD_CLASS_LIST = List.of(Serializer.class);

    /**
     * 加载所有的类对象
     */
    public static void loadAll() {
        log.info("加载所有 SPI");
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }

    /**
     * 从本地缓存中获取序列化器
     *
     * @return
     */
    public static <T> T gerInstance(Class<T> tClass, String key) {
        String tClassName = tClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(tClassName);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", tClassName));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 的 %s 不存在 key=%s的类型", tClassName, key));
        }
        //获取到要加载的实现类型
        Class<?> implClass = keyClassMap.get(key);
        //从实例缓存中加载指定类型的实例
        String implClassName = implClass.getName();
        if (!instanceCache.containsKey(implClassName)) {
            try {
                instanceCache.put(implClassName, implClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                String errorMsg = String.format("%s 类实例化失败", implClassName);
                throw new RuntimeException(errorMsg, e);
            }
        }
        return (T) instanceCache.get(implClassName);
    }

    /**
     * 加载单个对象
     *
     * @param loadClass
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("加载类型为 {} 的 SPI", loadClass);
        //扫描路径,用户自定义的 SPI 优先级高于系统 SPI
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String scanDir : SCAN_DIRS) {
            List<URL> resources = ResourceUtil.getResources(scanDir + loadClass.getName());
            try {
                //读取每个资源文件
                for (URL resource : resources) {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        String[] strArray = line.split("=");
                        if (strArray.length > 1) {
                            String key = strArray[0];
                            String className = strArray[1];
                            keyClassMap.put(key, Class.forName(className));
                        }
                    }
                }
            } catch (Exception e) {
                log.error("spi resource load error", e);
            }
        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;
    }
}
