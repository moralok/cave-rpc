package com.moralok.rpc.server.registry;

/**
 * Service Registry.
 *
 * @author moralok
 */
public interface ServiceRegistry {

    /**
     * Register a service.
     *
     * @param serviceName serviceKey
     */
    void registerService(String serviceName);

    void unregisterService(String serviceName);

    /**
     * Init.
     */
    void init();
}
