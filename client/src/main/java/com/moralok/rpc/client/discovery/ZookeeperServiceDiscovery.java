package com.moralok.rpc.client.discovery;

import com.moralok.rpc.common.Node;
import com.moralok.rpc.common.zookeeper.ZookeeperClient;

import java.util.List;
import java.util.stream.Collectors;

import static com.moralok.rpc.common.CommonConstants.*;

public class ZookeeperServiceDiscovery implements ServiceDiscovery {

    private final ZookeeperClient zkClient;

    public ZookeeperServiceDiscovery(ZookeeperClient zkClient) {
        this.zkClient = zkClient;
    }

    @Override
    public List<Node> getNodeList(String serviceName) {
        List<String> children = zkClient.getChildren(toPath(serviceName));
        return children.stream().map(url -> {
            String[] arr = url.split(COLON);
            return new Node(arr[0], Integer.parseInt(arr[1]));
        }).collect(Collectors.toList());
    }

    private String toPath(String serviceKey) {
        return PATH_SEPARATOR + CAVE + PATH_SEPARATOR + serviceKey + PATH_SEPARATOR + PROVIDERS_CATEGORY;
    }
}
