package com.kirisaki.kirisakirpc.utils;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.setting.dialect.Props;

/**
 * 配置工具类
 *
 */
public class ConfigUtils {
    private ConfigUtils(){

    }
    /**
     * 加载配置对象
     * @param tClass
     * @param prefix
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix) {
        return loadConfig(tClass, prefix, "");
    }

    /**
     * 加载配置对象,支持区分环境
     * @param tClass
     * @param prefix
     * @param environment
     * @return
     * @param <T>
     */
    public static <T> T loadConfig(Class<T> tClass, String prefix, String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (CharSequenceUtil.isNotBlank(environment)) {
            configFileBuilder.append("-").append(environment);
        }
        configFileBuilder.append(".properties");
        return new Props(configFileBuilder.toString()).toBean(tClass, prefix);
    }
}
