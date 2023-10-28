package com.moralok.rpc.client.loadbalance;

import com.moralok.rpc.common.Node;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * This class select one node from multiple nodes randomly.
 *
 * @author moralok
 */
public class RandomLoadBalance implements LoadBalance {

    @Override
    public Node select(List<Node> nodeList) {
        if (nodeList == null || nodeList.isEmpty()) {
            return null;
        }
        if (nodeList.size() == 1) {
            return nodeList.get(0);
        }
        int size = nodeList.size();
        int i = ThreadLocalRandom.current().nextInt(size);
        return nodeList.get(i);
    }
}
