package com.moralok.rpc.client.loadbalance;

import com.moralok.rpc.common.Node;

import java.util.List;

/**
 * LoadBalance
 *
 * @author moralok
 */
public interface LoadBalance {

    /**
     * Select one node in list
     *
     * @param nodeList node list
     * @return selected node
     */
    Node select(List<Node> nodeList);
}
