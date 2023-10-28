package com.moralok.rpc.client.discovery;

import com.moralok.rpc.common.Node;

import java.util.List;

/**
 * Service discovery
 *
 * @author moralok
 */
public interface ServiceDiscovery {

    /**
     * Get node list of the specified service.
     *
     * @param serviceName service name
     * @return node list of the specified service
     */
    List<Node> getNodeList(String serviceName);
}
