package com.kirisaki.kirisakirpc.server;

/**
 * HTTP 服务器接口
 */
public interface HttpServer {
    /**
     * 启动服务器
     * @param port 监听改的端口号
     */
    void doStart(int port);
}
