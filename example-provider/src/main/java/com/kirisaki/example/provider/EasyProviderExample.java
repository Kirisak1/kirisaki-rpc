package com.kirisaki.example.provider;

import com.kirisaki.example.common.service.UserService;
import com.kirisaki.kirisakirpc.server.HttpServer;
import com.kirisaki.kirisakirpc.server.VertxHttpServer;
import com.kirisaki.kirisakirpc.registry.LocalRegistry;

/**
 * 简易服务提供者示例
 */
public class EasyProviderExample {
    public static void main(String[] args) {
        //注册服务到本地服务器
        LocalRegistry.register(UserService.class.getName(), UserServiceImpl.class);

        HttpServer httpserver = new VertxHttpServer();
        httpserver.doStart(8080);

    }
}
