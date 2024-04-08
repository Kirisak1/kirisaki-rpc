package com.kirisaki.example.provider;

import com.kirisaki.example.common.service.UserService;
import com.kirisaki.kirisakirpc.RpcApplication;
import com.kirisaki.kirisakirpc.config.RegistryConfig;
import com.kirisaki.kirisakirpc.config.RpcConfig;
import com.kirisaki.kirisakirpc.model.ServiceMetaInfo;
import com.kirisaki.kirisakirpc.registry.LocalRegistry;
import com.kirisaki.kirisakirpc.registry.Registry;
import com.kirisaki.kirisakirpc.registry.RegistryFactory;
import com.kirisaki.kirisakirpc.server.VertxHttpServer;

/**
 * 服务提供者示例
 */
public class ProviderExample {
    public static void main(String[] args) {
        //RPC 框架初始化
        RpcApplication.init();

        //注册服务
        String serviceName = UserService.class.getName();
        LocalRegistry.register(serviceName, UserServiceImpl.class);

        //注册服务到注册中心
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getInstance(registryConfig.getRegistry());
        ServiceMetaInfo serviceMetaInfo = new ServiceMetaInfo();
        serviceMetaInfo.setServiceName(serviceName);
        serviceMetaInfo.setServiceHost(rpcConfig.getServerHost());
        serviceMetaInfo.setServicePort(rpcConfig.getServerPort());
        try {
            registry.register(serviceMetaInfo);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        //启动 web 服务
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        vertxHttpServer.doStart(rpcConfig.getServerPort());
    }

}
