> In 3-node environment, when replication.factor=3 is set, 
How does Kafka continue to run when one node is stopped?
> 

→3 노드 환경에서 replication.factor = 3으로 설정했을 때, 한 노드를 정지했는데 어떻게 계속 카프카 애플리케이션이 실행되는가?

### replication.factor = n

- 노드 수  
- 복제본을 n벌 갖겠다는 의미  

### min.insync.replica = n -1

- 장애가 날 시점에 대비하여, 몇 개의 동기화 된 노드를 가지고 있을 것인가?  
- 동기화 할 노드를 n-1 벌 갖겠다는 의미  

### acks

- acks=0,1 일 때는, 리더 한명만 있으면 replica.factor와 min.insync.replica에 상관 없이 계속 프로듀싱 된다.  
- acks=-1(all) 일 때는,  
    
    만약 노드 수가 3이고, min.insync.replica=3일 때, 한 노드가 죽으면 프로듀싱이 중지된다. 
    
    만약 노드 수가 3이고, min.insync.replica=2이면, 최대 1대 까지 죽어도 계속 프로듀싱 된다.

```
#replicator.factor=3
#acks=0 
#인 경우

$ cd /confluent/bin

$ ./kafka-topics --bootstrap-server broker02:9092 --describe --topic goodus

Topic: goodus	PartitionCount: 10	ReplicationFactor: 3	Configs: 
	Topic: goodus	Partition: 0	Leader: 103	Replicas: 101,103,102	Isr: 103,102	Offline: 101
	Topic: goodus	Partition: 1	Leader: 103	Replicas: 103,102,101	Isr: 103,102	Offline: 101
	Topic: goodus	Partition: 2	Leader: 102	Replicas: 102,101,103	Isr: 102,103	Offline: 101
	Topic: goodus	Partition: 3	Leader: 102	Replicas: 101,102,103	Isr: 102,103	Offline: 101
	Topic: goodus	Partition: 4	Leader: 103	Replicas: 103,101,102	Isr: 103,102	Offline: 101
	Topic: goodus	Partition: 5	Leader: 102	Replicas: 102,103,101	Isr: 102,103	Offline: 101
	Topic: goodus	Partition: 6	Leader: 103	Replicas: 101,103,102	Isr: 103,102	Offline: 101
	Topic: goodus	Partition: 7	Leader: 103	Replicas: 103,102,101	Isr: 103,102	Offline: 101
	Topic: goodus	Partition: 8	Leader: 102	Replicas: 102,101,103	Isr: 102,103	Offline: 101
	Topic: goodus	Partition: 9	Leader: 102	Replicas: 101,102,103	Isr: 102,103	Offline: 101

#복제본을 3벌 만들어놓았고, 동기화되고 있는 노드(ISR list)는 2벌이다. 

```

acks=1일 때에는 min.insync.replica 와 상관없이 계속 프로듀싱 된다.  
```
-- min.insync.replica = 2 / acks=1 

$ ./kafka-topics.sh --create --topic goodus02 --partitions 10 --replication-factor 3 --config min.insync.replicas=1 --bootstrap-server broker001:9092
$ ./kafka-topics.sh --describe --topic goodus02 --bootstrap-server broker001:9092
Topic: goodus02	PartitionCount: 10	ReplicationFactor: 3	Configs: min.insync.replicas=2,segment.bytes=1073741824
	Topic: goodus02	Partition: 0	Leader: 102	Replicas: 102,103,101	Isr: 102,103,101
	Topic: goodus02	Partition: 1	Leader: 101	Replicas: 101,102,103	Isr: 101,102,103
	Topic: goodus02	Partition: 2	Leader: 103	Replicas: 103,101,102	Isr: 103,101,102
	Topic: goodus02	Partition: 3	Leader: 102	Replicas: 102,101,103	Isr: 102,101,103
	Topic: goodus02	Partition: 4	Leader: 101	Replicas: 101,103,102	Isr: 101,103,102
	Topic: goodus02	Partition: 5	Leader: 103	Replicas: 103,102,101	Isr: 103,102,101
	Topic: goodus02	Partition: 6	Leader: 102	Replicas: 102,103,101	Isr: 102,103,101
	Topic: goodus02	Partition: 7	Leader: 101	Replicas: 101,102,103	Isr: 101,102,103
	Topic: goodus02	Partition: 8	Leader: 103	Replicas: 103,101,102	Isr: 103,101,102
	Topic: goodus02	Partition: 9	Leader: 102	Replicas: 102,101,103	Isr: 102,101,103

-- node 2, 3 down 후
$ ./kafka-topics.sh --describe --topic goodus02 --bootstrap-server broker001:9092
Topic: goodus02	PartitionCount: 10	ReplicationFactor: 3	Configs: min.insync.replicas=2,segment.bytes=1073741824
	Topic: goodus02	Partition: 0	Leader: 101	Replicas: 102,103,101	Isr: 101
	Topic: goodus02	Partition: 1	Leader: 101	Replicas: 101,102,103	Isr: 101
	Topic: goodus02	Partition: 2	Leader: 101	Replicas: 103,101,102	Isr: 101
	Topic: goodus02	Partition: 3	Leader: 101	Replicas: 102,101,103	Isr: 101
	Topic: goodus02	Partition: 4	Leader: 101	Replicas: 101,103,102	Isr: 101
	Topic: goodus02	Partition: 5	Leader: 101	Replicas: 103,102,101	Isr: 101
	Topic: goodus02	Partition: 6	Leader: 101	Replicas: 102,103,101	Isr: 101
	Topic: goodus02	Partition: 7	Leader: 101	Replicas: 101,102,103	Isr: 101
	Topic: goodus02	Partition: 8	Leader: 101	Replicas: 103,101,102	Isr: 101
	Topic: goodus02	Partition: 9	Leader: 101	Replicas: 102,101,103	Isr: 101

$ ./kafka-producer-perf-test.sh \
--topic goodus02 \
--num-records 100 \
--record-size 10 \
--throughput 100000 \
--producer-props acks=1 bootstrap.servers=broker001:9092,broker002:9092,broker003:9092 
100 records sent, 239.808153 records/sec (0.00 MB/sec), 25.86 ms avg latency, 406.00 ms max latency, 22 ms 50th, 24 ms 95th, 406 ms 99th, 406 ms 99.9th.

리더 101로 계속 프로듀싱 된다.
```

#####Test by Soyoen
