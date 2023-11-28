package com.moralok.rpc.common.zookeeper;

import java.util.List;

/**
 * Zookeeper client.
 *
 * @author moralok
 */
public interface ZookeeperClient {

    /**
     * Create node.
     *
     * @param path the path of node
     * @param ephemeral ephemeral or not
     */
    void create(String path, boolean ephemeral);

    /**
     * Get children of the node.
     *
     * @param path the path of node
     * @return null if node does not exist, otherwise return the children list
     */
    List<String> getChildren(String path);
}
