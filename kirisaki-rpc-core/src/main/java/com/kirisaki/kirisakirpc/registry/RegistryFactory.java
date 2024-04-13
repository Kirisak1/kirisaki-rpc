package com.kirisaki.kirisakirpc.registry;

import com.kirisaki.kirisakirpc.spi.SpiLoader;

/**
 * 注册器工厂  -> 允许上传自定义的 SPI
 */
public class RegistryFactory {
    /**
     *  初始化
     */
    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认的注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    /**
     * 加载指定的注册中心
     *
     * @param key
     * @return
     */
    public static Registry getInstance(String key) {
        return SpiLoader.gerInstance(Registry.class, key);
    }

}
