package com.kirisaki.kirisakirpc.server;

import io.vertx.core.Vertx;

/**
 * RPC 网络请求
 */
public class VertxHttpServer implements HttpServer {
    @Override
    public void doStart(int port) {
//看文档 https://vertx-china.github.io/docs/vertx-core/java/
        Vertx vertx = Vertx.vertx();

        io.vertx.core.http.HttpServer server = vertx.createHttpServer();
        //绑定请求处理器
        server.requestHandler(new HttpServerHandler());
        //监听端口并且响应服务开启的结果
        server.listen(port, result -> {
            if (result.succeeded()) {
                System.out.println("Service is now listening on  port " + port);
            } else {
                System.err.println("Failed to start server:" + result.cause());
            }
        });
    }
}
