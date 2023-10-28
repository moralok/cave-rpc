package com.moralok.rpc.common.zookeeper;

/**
 * Abstract class of zookeeper client.
 *
 * @author moralok
 */
public abstract class AbstractZookeeperClient implements ZookeeperClient {

    @Override
    public void create(String path, boolean ephemeral) {
        int i = path.lastIndexOf('/');
        if (i > 0) {
            create(path.substring(0, i), false);
        }
        if (ephemeral) {
            createEphemeral(path);
        } else {
            createPersistent(path);
        }
    }

    protected abstract void createPersistent(String path);

    protected abstract void createEphemeral(String path);

    protected abstract void deletePath(String path);
}
