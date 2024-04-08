package com.kirisaki.kirisakirpc.model;

import com.kirisaki.kirisakirpc.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * RPC 请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RpcRequest implements Serializable {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 方法名称
     */
    private String methodName;
    /**
     * 服务版本号
     */
    //todo 看一下这个字段是用来做什么的.. 检查拉取下的服务版本号是否正确?
    private String serviceVersion = RpcConstant.DEFAULT_SERVICE_VERSION;
    /**
     * 参数类型列表
     */
    private Class<?>[] parameterTypes;
    /**
     * 参数列表
     */
    private Object[] args;
}
