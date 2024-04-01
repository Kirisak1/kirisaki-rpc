package com.kirisaki.kirisakirpc.serializer;

import com.kirisaki.kirisakirpc.spi.SpiLoader;

/**
 * 序列化工厂
 */
public class SerializerFactory {

    // private static final Map<String, Serializer> KEY_SERIALIZER_MAP = new HashMap<>() {{
    //     put(SerializerKeys.JDK, new JdkSerializer());
    //     put(SerializerKeys.JSON, new JsonSerializer());
    //     put(SerializerKeys.KRYO, new KryoSerializer());
    //     put(SerializerKeys.HESSIAN, new HessianSerializer());
    // }};
    // /**
    //  * 默认序列化器
    //  */
    // private static final Serializer DEFAULT_SERIALIZER = KEY_SERIALIZER_MAP.get("jdk");
    //
    // /**
    //  * 获取实例
    //  *
    //  * @return
    //  */
    // public static Serializer getInstance(String key) {
    //     return KEY_SERIALIZER_MAP.getOrDefault(key, DEFAULT_SERIALIZER);
    // }


    static {
        SpiLoader.load(Serializer.class);
    }

    /**
     * 默认序列化器
     */

    private static final Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 获取实例
     *
     * @param key
     * @return
     */
    public static Serializer getInstance(String key) {
        return SpiLoader.gerInstance(Serializer.class, key);
    }

}

