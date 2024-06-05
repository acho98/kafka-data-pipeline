package com.example.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.apache.kafka.common.InvalidRecordException;

import java.util.Map;

public class CustomPartitioner  implements Partitioner {

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {

        if (keyBytes == null) {
            throw new InvalidRecordException("Need msg key");
        }
        if (((String)key).equals("Goodusdata01")){
            return 0;
        }
        if (((String)key).equals("Goodusdata02")){
            return 1;
        } else {
            return 2;
        }
    }

    @Override
    public void configure(Map<String, ?> configs) {}

    @Override
    public void close() {}
}