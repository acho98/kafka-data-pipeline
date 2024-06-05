package com.example.kafka;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Properties;

public class ConsumerCommit {
    public static final Logger logger = LoggerFactory.getLogger(ConsumerCommit.class.getName());

    public static void main(String[] args) {

        String topicName = "fake-topic20";

        Properties props = new Properties();
        props.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:29092");
        props.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        props.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "gx-03");
//        props.setProperty(ConsumerConfig.GROUP_INSTANCE_ID_CONFIG,"gid-100");
//        props.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
//        props.setProperty(ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, "60000");
        props.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG,"6000");
        props.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<String, String>(props);

        kafkaConsumer.subscribe(Arrays.asList(topicName));

        Thread mainThread = Thread.currentThread();
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                logger.info("main program starts to exit by calling wakeup");
                kafkaConsumer.wakeup();

                try {
                    mainThread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });


//        pollAutoCommit(kafkaConsumer);
//        pollCommitSync(kafkaConsumer);
        pollCommitAsync(kafkaConsumer);


    }

    private static void pollCommitAsync(KafkaConsumer<String, String> kafkaConsumer) {
        int loopCnt = 0;

        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
                logger.info(" ############# loopCnt: {}, consumerRecord count: {}", loopCnt++, consumerRecords.count());
                for (ConsumerRecord record : consumerRecords) {
                    logger.info("record key:{}, partition:{}, offset:{}, record value:{}",
                            record.key(), record.partition(), record.offset(), record.value());
                }
                kafkaConsumer.commitAsync(new OffsetCommitCallback() {
                    @Override
                    public void onComplete(Map<TopicPartition, OffsetAndMetadata> offsets, Exception exception) {
                        if(exception != null) {
                            logger.error("offsets {} is not completed, error:{}", offsets, exception.getMessage());
                        }
                    }
                });

            }
        } catch (WakeupException e) {
            logger.error("wakeup exception has been called");
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            kafkaConsumer.commitSync();
            logger.info("finally consumer is closing");
            kafkaConsumer.close();
        }

    }

    private static void pollCommitSync(KafkaConsumer<String, String> kafkaConsumer) {
        int loopCnt = 0;

        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
                logger.info(" ############# loopCnt: {}, consumerRecord count: {}", loopCnt++, consumerRecords.count());
                for (ConsumerRecord record : consumerRecords) {
                    logger.info("record key:{}, partition:{}, offset:{}, record value:{}",
                            record.key(), record.partition(), record.offset(), record.value());
                }
                try {
                    if(consumerRecords.count() > 0) {
                        kafkaConsumer.commitSync();
                        logger.info("commit sync has been called");
                    }
                } catch (CommitFailedException e) {
                    logger.error(e.getMessage());
                }
            }
        } catch (WakeupException e) {
            logger.error("wakeup exception has been called");
        } catch (Exception e) {
            logger.error(e.getMessage());
        } finally {
            logger.info("finally consumer is closing");
            kafkaConsumer.close();
        }
    }

    public static void pollAutoCommit(KafkaConsumer<String, String> kafkaConsumer) {
        int loopCnt = 0;

        try {
            while (true) {
                ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(1000));
                logger.info(" ############# loopCnt: {}, consumerRecord count: {}", loopCnt++, consumerRecords.count());
                for (ConsumerRecord record : consumerRecords) {
                    logger.info("record key:{}, partition:{}, offset:{}, record value:{}",
                            record.key(), record.partition(), record.offset(), record.value());
                }
                try {
                    logger.info("main thread is sleeping {} ms during while loop");
                    Thread.sleep(10000);
                }catch(InterruptedException e){
                    e.printStackTrace();
                }
            }
        } catch (WakeupException e) {
            logger.error("wakeup exception has been called");
        } finally {
            logger.info("finally consumer is closing");
            kafkaConsumer.close();
        }
    }
}

