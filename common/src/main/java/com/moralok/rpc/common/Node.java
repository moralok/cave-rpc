package com.moralok.rpc.common;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

/**
 * Server Node
 *
 * @author moralok
 */
public class Node implements Serializable {

    @Serial
    private static final long serialVersionUID = 8965160218331161916L;

    /**
     * host
     */
    private String host;

    /**
     * port
     */
    private int port;

    public Node(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    @Override
    public String toString() {
        return "Node{" +
                "host='" + host + '\'' +
                ", port=" + port +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Node node = (Node) o;

        if (port != node.port) return false;
        return Objects.equals(host, node.host);
    }

    @Override
    public int hashCode() {
        int result = host != null ? host.hashCode() : 0;
        result = 31 * result + port;
        return result;
    }
}
