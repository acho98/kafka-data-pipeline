## Listener 설정

- kafka 공식 사이트 예제
```
listener.security.protocol.map=CLIENT:SASL_PLAINTEXT,REPLICATION:PLAINTEXT,INTERNAL_PLAINTEXT:PLAINTEXT,INTERNAL_SASL:SASL_PLAINTEXT
advertised.listeners=CLIENT://cluster1.foo.com:9092,REPLICATION://broker1.replication.local:9093,INTERNAL_PLAINTEXT://broker1.local:9094,INTERNAL_SASL://broker1.local:9095
listeners=CLIENT://192.1.1.8:9092,REPLICATION://10.1.1.5:9093,INTERNAL_PLAINTEXT://10.1.1.5:9094,INTERNAL_SASL://10.1.1.5:9095
```

- Server Example
```
listeners=CLIENT://kafka1:9092,EXT://broker01:9093
advertised.listeners=CLIENT://100.100.100.1:9092,EXT://100.100.100.2:9093
listener.security.protocol.map=CLIENT:PLAINTEXT,EXT:PLAINTEXT
inter.broker.listener.name=CLIENT
```

- inter.broker.listener.name 반드시 설정

## Test
- Connecting on port 9092(which we map as LISTENER_FRED), the broker's address is given back as localhost:
```
$kafkacat -b kafka0:9092 \

Metadata for all topics (from broker -1: kafka0:9092/bootstrap):
1 brokers:
  broker 0 at localhost:9092
```

- Connecting on port 29092(which we map as LISTENER_BOB), the broker's address is given back as kafka0:
```
$ kafkacat -b kafka0:29092 \

Metadata for all topics (from broker 0: kafka0:29092/0):
1 brokers:
  broker 0 at kafka0:29092
```

## Ref
https://stackoverflow.com/questions/42998859/kafka-server-configuration-listeners-vs-advertised-listeners  
https://rmoff.net/2018/08/02/kafka-listeners-explained/  
https://www.confluent.io/blog/kafka-listeners-explained/  
