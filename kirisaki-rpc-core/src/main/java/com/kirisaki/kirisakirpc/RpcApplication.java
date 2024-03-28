package com.kirisaki.kirisakirpc;

import com.kirisaki.kirisakirpc.config.RpcConfig;
import com.kirisaki.kirisakirpc.constant.RpcConstant;
import com.kirisaki.kirisakirpc.utils.ConfigUtils;
import lombok.extern.slf4j.Slf4j;

/**
 * Rpc
 */
@Slf4j
public class RpcApplication {
    /**
     * 全局配置对象
     */
    private static volatile RpcConfig rpcConfig;

    /**
     * 框架初始化,支持掺入自定义配置
     *
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig) {
        rpcConfig = newRpcConfig;
        log.info("rpc init,config = {}", newRpcConfig.toString());
    }

    /**
     * 初始化

     */
    public static void init() {
        RpcConfig  newRpcConfig;
        try {
            newRpcConfig = ConfigUtils.loadConfig(RpcConfig.class, RpcConstant.DEFAULT_CONFIG_PREFIX);
        }catch (Exception e){
            //配置加载失败,使用默认值
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
    }

    /**
     * 获取配置
     * @return
     */
    public static RpcConfig getRpcConfig() {
        if (rpcConfig == null) {
            synchronized (RpcApplication.class) {
                if (rpcConfig == null) {
                    init();
                }
            }
        }
        return rpcConfig;
    }
}
