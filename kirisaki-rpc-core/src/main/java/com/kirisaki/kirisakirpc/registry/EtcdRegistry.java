package com.kirisaki.kirisakirpc.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.kirisaki.kirisakirpc.config.RegistryConfig;
import com.kirisaki.kirisakirpc.model.ServiceMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Etcd 实现注册中心
 */
public class EtcdRegistry implements Registry {
    /**
     * 本机注册的节点 key 集合(用于维护续期)
     */
    private final Set<String> localRegisterNodeKeySet = new HashSet<>();
    /**
     * 服务缓存(消费端)
     */
    private final ConsumeRegistry consumeRegistry = new ConsumeRegistry();
    /**
     * 客户端对象
     */
    public static Client client;
    /**
     * 操作 kv 对象客户端
     */
    public static KV kvClient;
    /**
     * 根节点
     */
    private static final String ETCD_ROOT_PATH = "/rpc/";

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .endpoints(registryConfig.getAddress()).connectTimeout(Duration.ofMillis(registryConfig.getTimeout()))
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void register(ServiceMetaInfo serviceMetaInfo) throws Exception {
        //创建 Lease 和 KV 客户端
        Lease leaseClient = client.getLeaseClient();

        //创建一个 30 秒的租约
        long leaseId = leaseClient.grant(30).get().getID();

        //设置要存储的键值对
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        ByteSequence key = ByteSequence.from(registerKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(serviceMetaInfo), StandardCharsets.UTF_8);

        //将键值对与租约关联起来,并设置过期时间
        PutOption putOption = PutOption.builder().withLeaseId(leaseId).build();
        kvClient.put(key, value, putOption).get();

        //添加节点信息到本地缓存
        localRegisterNodeKeySet.add(registerKey);
    }

    @Override
    public void unRegister(ServiceMetaInfo serviceMetaInfo) {
        String registerKey = ETCD_ROOT_PATH + serviceMetaInfo.getServiceNodeKey();
        kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8));

        //服务注册表删除服务注册的信息
        localRegisterNodeKeySet.remove(registerKey);

        //todo  本地缓存也需要删除 这里不确定是否需要注销
        consumeRegistry.destroy();
    }

    @Override
    public List<ServiceMetaInfo> serviceDiscovery(String serviceKey) {


        //如果有本地缓存优先读取本地缓存
        if (consumeRegistry.registryCache != null) {
            return consumeRegistry.registryCache;
        }

        //前缀搜索,结尾一定要加'/'
        String searchPrefix = ETCD_ROOT_PATH + serviceKey + "/";
        try {
            //支持前缀搜索
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> keyValues = kvClient.get(
                            ByteSequence.from(searchPrefix,
                                    StandardCharsets.UTF_8),
                            getOption)
                    .get()
                    .getKvs();
            List<ServiceMetaInfo> queryResult = keyValues.stream()
                    .map(keyValue -> {
                        String value =
                                keyValue.getValue().toString(StandardCharsets.UTF_8);
                        return JSONUtil.toBean(value, ServiceMetaInfo.class);
                    }).collect(Collectors.toList());

            //服务监控
            Watch watchClient = client.getWatchClient();
            for (ServiceMetaInfo serviceMetaInfo : queryResult) {
                String serviceNodeKey = serviceMetaInfo.getServiceNodeKey();
                watchClient.watch(ByteSequence.from(serviceNodeKey, StandardCharsets.UTF_8), (response) -> {
                    for (WatchEvent event : response.getEvents()) {
                        switch (event.getEventType()) {
                            case DELETE:
                            case PUT:
                                consumeRegistry.registryCache = null;
                                break;
                            default:
                                break;
                        }
                    }
                });
            }

            //写入缓存
            consumeRegistry.write(queryResult);

            return queryResult;
        } catch (Exception e) {
            throw new RuntimeException("获取列表失败", e);
        }

    }

    @Override
    public void destroy() {
        System.out.println("当前节点下线");
        for (String key : localRegisterNodeKeySet) {
            try {
                kvClient.delete(ByteSequence.from(key, StandardCharsets.UTF_8)).get();
            } catch (Exception e) {
                throw new RuntimeException(key + "节点下线失败");
            }
        }
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        // 10秒续签一次
        CronUtil.schedule("*/10 * * * * *", (Task) () -> {
            //遍历本节点所有的 key
            for (String key : localRegisterNodeKeySet) {
                try {
                    List<KeyValue> keyValues = kvClient.get(ByteSequence.from(key, StandardCharsets.UTF_8))
                            .get()
                            .getKvs();
                    //该节点已过期(需要重启节点才能重新注册)
                    if (CollUtil.isEmpty(keyValues)) {
                        continue;
                    }
                    KeyValue keyValue = keyValues.get(0);
                    String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                    ServiceMetaInfo serviceMetaInfo = JSONUtil.toBean(value, ServiceMetaInfo.class);
                    register(serviceMetaInfo);
                } catch (Exception e) {
                    throw new RuntimeException(key + "签约失败", e);
                }
            }
        });
        // 支持秒级别定时任务
        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }
}
