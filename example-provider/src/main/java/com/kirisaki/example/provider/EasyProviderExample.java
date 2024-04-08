package com.kirisaki.example.provider;

import com.kirisaki.example.common.service.UserService;
import com.kirisaki.kirisakirpc.RpcApplication;
import com.kirisaki.kirisakirpc.registry.LocalRegistry;
import com.kirisaki.kirisakirpc.server.HttpServer;
import com.kirisaki.kirisakirpc.server.VertxHttpServer;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        RpcApplication.init();
        //注册服务到本地服务器
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);
        //注册中心
        RpcApplication.init();
        HttpServer httpserver = new VertxHttpServer();
        httpserver.doStart(8080);
    }
}
