package com.moralok.rpc.common.serialization;

/**
 * Interface of Serializer.
 *
 * @author moralok
 */
public interface Serializer {

    /**
     * Serialize object into bytes.
     *
     * @param object object
     * @return byte data
     * @param <T> generic parameter
     */
    <T> byte[] serialize(T object);

    /**
     * Deserialize bytes into object
     *
     * @param bytes byte data
     * @param clazz the Class of object
     * @return object
     * @param <T> generic parameter
     */
    <T> T deserialize(byte[] bytes, Class<T> clazz);
}
