package com.example.kafka;

import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

public class TxProducer {
    public static final Logger logger = LoggerFactory.getLogger(TxProducer.class.getName());
    public static void main(String[] args) {

        Properties props = new Properties();
        props.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,"v2-kafka1:29092");
        props.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.setProperty(ProducerConfig.TRANSACTIONAL_ID_CONFIG,"transaction-01");

        KafkaProducer<String, String> kafkaProducer = new KafkaProducer<String, String>(props);

        kafkaProducer.initTransactions();
        kafkaProducer.beginTransaction();
        try {
            for(int i = 0; i <= 5; i++){
                String msgKey = "id-" + String.valueOf(i);
                ProducerRecord<String, String> producerRecord = new ProducerRecord<>("transaction", msgKey,"messages " + i);
                kafkaProducer.send(producerRecord);
                kafkaProducer.flush();
            }
        }catch (Exception e){
            kafkaProducer.abortTransaction();
            logger.error(e.getMessage());
        }finally {
            kafkaProducer.commitTransaction();
            kafkaProducer.close();
        }
    }
}
