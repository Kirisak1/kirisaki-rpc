package com.kirisaki.kirisakirpc.config;

import lombok.Data;

/**
 * 封装 Rpc配置文件对象
 */
@Data
public class RpcConfig {

    private String name = "kirisaki";
    /**
     * 版本号
     */
    private String version = "1.0";
    /**
     * 服务名称
     */
    private String serverHost = "localhost";
    /**
     * 服务端口
     */
    private Integer serverPort = 8080;
    /**
     * 是否开启 mock 数据
     */
    private boolean mock = false;
}
