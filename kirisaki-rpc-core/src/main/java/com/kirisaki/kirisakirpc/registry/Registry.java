package com.kirisaki.kirisakirpc.registry;

import com.kirisaki.kirisakirpc.config.RegistryConfig;
import com.kirisaki.kirisakirpc.model.ServiceMetaInfo;

import java.util.List;

/**
 * 注册中心接口
 */
public interface Registry {
    /**
     * 初始化
     */
    void init(RegistryConfig registryConfig);

    /**
     * 注册服务 (服务端)
     *
     * @param serviceMetaInfo
     */
    void register(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 注销(服务端)
     *
     * @param serviceMetaInfo
     */
    void unRegister(ServiceMetaInfo serviceMetaInfo) throws Exception;

    /**
     * 服务发现 (获取某服务的所有节点,消费端)
     *
     * @param serviceKey
     * @return
     */
    List<ServiceMetaInfo> serviceDiscovery(String serviceKey);

    /**
     * 注销
     */
    void destroy();
}
