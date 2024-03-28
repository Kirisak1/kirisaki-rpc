package com.kirisaki.example.consumer;

import com.kirisaki.kirisakirpc.config.RpcConfig;
import com.kirisaki.kirisakirpc.utils.ConfigUtils;

public class ConsumerExample {
    public static void main(String[] args) {
        RpcConfig rpc = ConfigUtils.loadConfig(RpcConfig.class, "rpc");
        System.out.println(rpc);
    }
}
