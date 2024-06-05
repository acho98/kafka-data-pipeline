## Issue
브로커가 정상적으로 실행되지 않음  

## Logs
```
[202x-xx-xx 14:22:48,910] ERROR [KafkaServer id=101] Fatal error during KafkaServer startup. Prepare to shutdown (kafka.server.KafkaServer)
java.lang.IllegalStateException: Kafka HTTP server failed to start up.
at kafka.server.KafkaServer.startup(KafkaServer.scala:603)
at kafka.server.KafkaServerStartable.startup(KafkaServerStartable.scala:44)
at kafka.Kafka$.main(Kafka.scala:82)
at kafka.Kafka.main(Kafka.scala)
[202x-xx-xx 14:22:49,266] INFO [KafkaServer id=101] shutting down (kafka.server.KafkaServer)
```

## Cause
HTTP 서버가 기본 제한 시간인 30초 내에 시작할 수 없어서 발생

## Solution
```
confluent.http.server.start.timeout.ms=120000
```

- server.properties에 다음 파라미터 추가  
- HTTP 서버에 30초 제한이 있어서 그 시간을 늘려줌 (브로커가 HTTP 서버 시작에 더 많은 시간을 할애할 수 있도록 증가시킴)  
- [confluent.http.server.start.timeout.ms](http://confluent.http.server.start.timeout.ms/) kafka Broker가 호스팅하는 HTTP 서버 시작을 위해 설정된 내부 시간 제한  

Comment by Soyeon

