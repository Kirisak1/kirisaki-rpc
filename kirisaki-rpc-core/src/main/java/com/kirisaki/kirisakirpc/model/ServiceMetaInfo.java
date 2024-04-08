package com.kirisaki.kirisakirpc.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;

/**
 * 服务元信息
 */
@Data
public class ServiceMetaInfo {
    /**
     * 服务名称
     */
    private String serviceName;
    /**
     * 版本号
     */
    private String serviceVersion = "1.0";
    /**
     * 服务域名
     */
    private String serviceHost = "localhost";
    /**
     * 服务端口号
     */
    private Integer servicePort = 8080;

    /**
     * 服务分组 (暂未实现)
     */
    private String serviceGroup = "default";

    /**
     * 服务器前缀 -> 用于前缀搜索
     *
     * @return
     */
    public String getServiceKey() {
        //后续可拓展服务分组
        // return String.format("%s:%s:%s", serviceName, serviceVersion, serviceGroup);
        return String.format("%s:%s", serviceName, serviceVersion);
    }

    /**
     * 某个具体的服务器地址
     *
     * @return
     */
    public String getServiceNodeKey() {
        return String.format("%s/%s:%s", getServiceKey(), serviceHost, servicePort);
    }

    public String getServiceAddress() {
        if (!StrUtil.contains(serviceHost, "http://")) {
            return String.format("http://%s:%s", serviceHost, servicePort);
        }
        return String.format("%s:%s", serviceHost, servicePort);
    }
}
