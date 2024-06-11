 
- Consuming 할 Topic 생성 (apache rh1)  
```
$ ./kafka-topics.sh --bootstrap-server rh1:9092 --topic sy.noconsumer.gid --create
```
- Sample message producing  
```
$ ./kafka-console-producer.sh --bootstrap-server rh1:9092 --topic sy.noconsumer.gid
>consuemr id : 1
>consumer id : 2
>consuemr id : 2
>consumer id : 4
```

- Run Consumer
```
package com.example;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Arrays;
import java.util.Properties;

public class SimpleConsumer {
    private final static Logger logger = LoggerFactory.getLogger(SimpleConsumer.class);
    private final static String TOPIC_NAME = "sy.noconsumer.gid";
    private final static String BOOTSTRAP_SERVERS = "10.10.10.1:9092";
    private final static String GROUP_ID = "";

    public static void main(String args[]) {
        Properties configs = new Properties();
        configs.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        configs.put(ConsumerConfig.GROUP_ID_CONFIG, GROUP_ID);
        configs.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        configs.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        configs.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(configs);
        consumer.subscribe(Arrays.asList(TOPIC_NAME));

        while (true) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));
            for (ConsumerRecord<String, String> record : records) {
                logger.info("{}", record);
            }
        }
    }
}
```
** Run without consumer group id → 컨슈밍 안됨 **

```
Execution failed for task ':SimpleConsumer.main()'.
> Process 'command '/Library/Java/JavaVirtualMachines/jdk1.8.0_291.jdk/Contents/Home/bin/java'' finished with non-zero exit value 1

* Try:
Run with --stacktrace option to get the stack trace. Run with --info or --debug option to get more log output. Run with --scan to get full insights.
```
consumer group id 값을 null로 두는 옵션은 deprecated 된다고 한다.  

- Run with consumer group id → 컨슈밍 됨  
```
14:19:15: Executing task 'SimpleConsumer.main()'...

> Task :compileJava
> Task :processResources NO-SOURCE
> Task :classes

> Task :SimpleConsumer.main()
[main] INFO org.apache.kafka.clients.consumer.ConsumerConfig - ConsumerConfig values: 
	allow.auto.create.topics = true
	auto.commit.interval.ms = 5000
	auto.offset.reset = earliest
	bootstrap.servers = [10.10.10.1:9092]
	check.crcs = true
	client.dns.lookup = use_all_dns_ips
	client.id = consumer-sy1-1
	client.rack = 
	connections.max.idle.ms = 540000
	default.api.timeout.ms = 60000
	enable.auto.commit = true
	exclude.internal.topics = true
	fetch.max.bytes = 52428800
	fetch.max.wait.ms = 500
	fetch.min.bytes = 1
	group.id = sy1
	group.instance.id = null
	heartbeat.interval.ms = 3000
	interceptor.classes = []
	internal.leave.group.on.close = true
	internal.throw.on.fetch.stable.offset.unsupported = false
	isolation.level = read_uncommitted
	key.deserializer = class org.apache.kafka.common.serialization.StringDeserializer
	max.partition.fetch.bytes = 1048576
	max.poll.interval.ms = 300000
	max.poll.records = 500
	metadata.max.age.ms = 300000
	metric.reporters = []
	metrics.num.samples = 2
	metrics.recording.level = INFO
	metrics.sample.window.ms = 30000
	partition.assignment.strategy = [class org.apache.kafka.clients.consumer.RangeAssignor]
	receive.buffer.bytes = 65536
	reconnect.backoff.max.ms = 1000
	reconnect.backoff.ms = 50
	request.timeout.ms = 30000
	retry.backoff.ms = 100
	sasl.client.callback.handler.class = null
	sasl.jaas.config = null
	sasl.kerberos.kinit.cmd = /usr/bin/kinit
	sasl.kerberos.min.time.before.relogin = 60000
	sasl.kerberos.service.name = null
	sasl.kerberos.ticket.renew.jitter = 0.05
	sasl.kerberos.ticket.renew.window.factor = 0.8
	sasl.login.callback.handler.class = null
	sasl.login.class = null
	sasl.login.refresh.buffer.seconds = 300
	sasl.login.refresh.min.period.seconds = 60
	sasl.login.refresh.window.factor = 0.8
	sasl.login.refresh.window.jitter = 0.05
	sasl.mechanism = GSSAPI
	security.protocol = PLAINTEXT
	security.providers = null
	send.buffer.bytes = 131072
	session.timeout.ms = 10000
	socket.connection.setup.timeout.max.ms = 127000
	socket.connection.setup.timeout.ms = 10000
	ssl.cipher.suites = null
	ssl.enabled.protocols = [TLSv1.2]
	ssl.endpoint.identification.algorithm = https
	ssl.engine.factory.class = null
	ssl.key.password = null
	ssl.keymanager.algorithm = SunX509
	ssl.keystore.certificate.chain = null
	ssl.keystore.key = null
	ssl.keystore.location = null
	ssl.keystore.password = null
	ssl.keystore.type = JKS
	ssl.protocol = TLSv1.2
	ssl.provider = null
	ssl.secure.random.implementation = null
	ssl.trustmanager.algorithm = PKIX
	ssl.truststore.certificates = null
	ssl.truststore.location = null
	ssl.truststore.password = null
	ssl.truststore.type = JKS
	value.deserializer = class org.apache.kafka.common.serialization.StringDeserializer

[main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka version: 2.7.0
[main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka commitId: 448719dc99a19793
[main] INFO org.apache.kafka.common.utils.AppInfoParser - Kafka startTimeMs: 1648617557333
[main] INFO org.apache.kafka.clients.consumer.KafkaConsumer - [Consumer clientId=consumer-sy1-1, groupId=sy1] Subscribed to topic(s): sy.noconsumer.gid
[main] INFO org.apache.kafka.clients.Metadata - [Consumer clientId=consumer-sy1-1, groupId=sy1] Cluster ID: YyDY7xCfR9CMDDk919pbjg
[main] INFO org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Discovered group coordinator rh3:9092 (id: 2147483544 rack: null)
[main] INFO org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] (Re-)joining group
[main] INFO org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] (Re-)joining group
[main] INFO org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Successfully joined group with generation Generation{generationId=1, memberId='consumer-sy1-1-085f5c19-fead-43d4-9efd-cab79bb3132a', protocol='range'}
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Finished assignment for group at generation 1: {consumer-sy1-1-085f5c19-fead-43d4-9efd-cab79bb3132a=Assignment(partitions=[sy.noconsumer.gid-0, sy.noconsumer.gid-1, sy.noconsumer.gid-2])}
[main] INFO org.apache.kafka.clients.consumer.internals.AbstractCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Successfully synced group in generation Generation{generationId=1, memberId='consumer-sy1-1-085f5c19-fead-43d4-9efd-cab79bb3132a', protocol='range'}
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Notifying assignor about the new Assignment(partitions=[sy.noconsumer.gid-0, sy.noconsumer.gid-1, sy.noconsumer.gid-2])
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Adding newly assigned partitions: sy.noconsumer.gid-2, sy.noconsumer.gid-1, sy.noconsumer.gid-0
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Found no committed offset for partition sy.noconsumer.gid-2
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Found no committed offset for partition sy.noconsumer.gid-1
[main] INFO org.apache.kafka.clients.consumer.internals.ConsumerCoordinator - [Consumer clientId=consumer-sy1-1, groupId=sy1] Found no committed offset for partition sy.noconsumer.gid-0
[main] INFO org.apache.kafka.clients.consumer.internals.SubscriptionState - [Consumer clientId=consumer-sy1-1, groupId=sy1] Resetting offset for partition sy.noconsumer.gid-0 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[rh3:9092 (id: 103 rack: null)], epoch=0}}.
[main] INFO org.apache.kafka.clients.consumer.internals.SubscriptionState - [Consumer clientId=consumer-sy1-1, groupId=sy1] Resetting offset for partition sy.noconsumer.gid-2 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[rh1:9092 (id: 101 rack: null)], epoch=0}}.
[main] INFO org.apache.kafka.clients.consumer.internals.SubscriptionState - [Consumer clientId=consumer-sy1-1, groupId=sy1] Resetting offset for partition sy.noconsumer.gid-1 to position FetchPosition{offset=0, offsetEpoch=Optional.empty, currentLeader=LeaderAndEpoch{leader=Optional[rh2:9092 (id: 102 rack: null)], epoch=0}}.
[main] INFO com.example.SimpleConsumer - ConsumerRecord(topic = sy.noconsumer.gid, partition = 1, leaderEpoch = 0, offset = 0, CreateTime = 1648617194782, serialized key size = -1, serialized value size = 15, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = consumer id : 2)
[main] INFO com.example.SimpleConsumer - ConsumerRecord(topic = sy.noconsumer.gid, partition = 1, leaderEpoch = 0, offset = 1, CreateTime = 1648617210647, serialized key size = -1, serialized value size = 15, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = consumer id : 4)
[main] INFO com.example.SimpleConsumer - ConsumerRecord(topic = sy.noconsumer.gid, partition = 2, leaderEpoch = 0, offset = 0, CreateTime = 1648617189681, serialized key size = -1, serialized value size = 15, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = consuemr id : 1)
[main] INFO com.example.SimpleConsumer - ConsumerRecord(topic = sy.noconsumer.gid, partition = 2, leaderEpoch = 0, offset = 1, CreateTime = 1648617201419, serialized key size = -1, serialized value size = 15, headers = RecordHeaders(headers = [], isReadOnly = false), key = null, value = consuemr id : 2)
```


