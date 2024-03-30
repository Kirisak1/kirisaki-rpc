package com.kirisaki.kirisakirpc.serializer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kirisaki.kirisakirpc.model.RpcRequest;
import com.kirisaki.kirisakirpc.model.RpcResponse;

import java.io.IOException;

/**
 * Json 序列化器
 */
public class JsonSerializer implements Serializer {
    //todo 需要自己多引入一个依赖? 不明白为什么文档里不需要引入依赖
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    @Override
    public <T> byte[] serialize(T object) throws IOException {
        return OBJECT_MAPPER.writeValueAsBytes(object);
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> classType) throws IOException {
        T obj = OBJECT_MAPPER.readValue(bytes, classType);
        if (obj instanceof RpcRequest) {
            return handleRequest((RpcRequest) obj, classType);
        }
        if (obj instanceof RpcResponse) {
            return handleResponse((RpcResponse) obj, classType);
        }
        return obj;
    }

    private <T> T handleRequest(RpcRequest rpcRequest, Class<T> type) throws IOException{
        Class<?>[] parameterTypes = rpcRequest.getParameterTypes();
        Object[] args = rpcRequest.getArgs();

        //循环处理每个参数的类型
        for (int i = 0; i < parameterTypes.length; i++) {
            Class<?> clazz = parameterTypes[i];
            //如果类型不同,则重新处理下类型
            if (!clazz.isAssignableFrom(args[i].getClass())) {
                byte[] argBytes = OBJECT_MAPPER.writeValueAsBytes(args[i]);
                args[i] = OBJECT_MAPPER.readValue(argBytes, clazz);
            }
        }
        return type.cast(rpcRequest);
    }

    /**
     * 由于 Object 的原始对象会被擦除,导致反序列化时会被作为 LinkedHashMap 无法转换成原始对象,因此这里做了特殊处理
     * @param rpcResponse rpc 响应
     * @param type 类型
     * @return{@link T}
     * @throws IOException IO 异常
     */
    private <T> T handleResponse(RpcResponse rpcResponse, Class<T> type)throws IOException {
        byte[] dataBytes = OBJECT_MAPPER.writeValueAsBytes(rpcResponse.getData());
        rpcResponse.setData(OBJECT_MAPPER.readValue(dataBytes, rpcResponse.getDataType()));
        return type.cast(rpcResponse);
    }
}