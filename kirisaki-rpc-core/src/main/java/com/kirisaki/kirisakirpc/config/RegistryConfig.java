package com.kirisaki.kirisakirpc.config;

import com.kirisaki.kirisakirpc.registry.RegistryKeys;
import lombok.Data;

/**
 * 注册中心配置
 */
@Data
public class RegistryConfig {
    /**
     * 注册中心类别
     */
    private String registry = RegistryKeys.ETCD;
    /**
     * 地址
     */
    private String address = "http://localhost:2380";
    /**
     * 账号
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 连接时间
     */
    private Long timeout = 10000L;
}

