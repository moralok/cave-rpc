package com.moralok.rpc.common.util;


/**
 * Generate unique ID.
 */
public class Snowflake {

    private final long workerIdBits = 5L;
    private final long datacenterIdBits = 5L;
    private final long maxWorkerId = ~(-1L << workerIdBits);
    private final long maxDatacenterId = ~(-1L << datacenterIdBits);
    private final long sequenceIdBits = 12L;
    private final long sequenceIdMask = ~(-1L << sequenceIdBits);
    private final long workerIdShift = sequenceIdBits;
    private final long datacenterIdShift = sequenceIdBits + workerIdBits;
    private final long timestampShift = sequenceIdBits + workerIdBits + datacenterIdBits;

    private long lastTimestamp = -1L;
    private final long startTimestamp = 1698332799000L;

    private final long datacenterId;

    private final long workerId;

    private long sequenceId;

    public Snowflake(long datacenterId, long workerId, long sequenceId) {
        if (workerId > maxWorkerId || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0",maxWorkerId));
        }
        if (datacenterId > maxDatacenterId || datacenterId < 0) {
            throw new IllegalArgumentException(String.format("datacenterId can't be greater than %d or less than 0",maxDatacenterId));
        }
        System.out.printf("worker starting. timestamp left shift %d, datacenterId bits %d, workerId bits %d, sequenceId bits %d, datacenterId %d, workerId %d",
                timestampShift, datacenterIdBits, workerIdBits, sequenceIdBits, datacenterId, workerId);
        this.datacenterId = datacenterId;
        this.workerId = workerId;
        this.sequenceId = sequenceId;
    }

    public long getDatacenterId() {
        return datacenterId;
    }

    public long getWorkerId() {
        return workerId;
    }

    public long getSequenceId() {
        return sequenceId;
    }

    public synchronized long nextId() {
        long timestamp = generateTimestamp();

        if (timestamp < lastTimestamp) {
            throw new RuntimeException(String.format("Clock moved backwards.  Refusing to generate id for %d milliseconds",
                    lastTimestamp - timestamp));
        }

        if (lastTimestamp == timestamp) {
            sequenceId = (sequenceId + 1) & sequenceIdMask;
            if (sequenceId == 0) {
                timestamp = waitUntilNextMillis(lastTimestamp);
            }
        } else {
            sequenceId = 0;
        }
        lastTimestamp = timestamp;
        return ((timestamp - startTimestamp) << timestampShift)
                | (datacenterId << datacenterIdShift)
                | (workerId << workerIdShift)
                | sequenceId;
    }

    private long waitUntilNextMillis(long lastTimestamp) {
        long timestamp = generateTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = generateTimestamp();
        }
        return timestamp;
    }

    private long generateTimestamp() {
        return System.currentTimeMillis();
    }
}
