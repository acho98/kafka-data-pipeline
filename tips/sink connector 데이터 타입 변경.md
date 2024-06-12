## 1. 현상

- Decimal(10진수) 타입의 데이터가 깨져서 보임

[C3] REGION_ID는 10진수인데, 확인 불가  
![console](../../images/sink_type/sinktype1.png)

[Compass] REGION_ID는 10진수인데, 소수점 보임  
![console](../../images/sink_type/sinktype2.png)

## 2. 해결

- SMT 사용, Connector에 다음 파라미터 추가  
```
"transforms": "Cast",
"transforms.Cast.type": "org.apache.kafka.connect.transforms.Cast$Value",
"transforms.Cast.spec": "fieldName:float64"
```

## 3. 메뉴얼

### Sink Connector
https://www.confluent.io/hub/confluentinc/connect-transforms

https://docs.confluent.io/platform/current/connect/transforms/cast.html

- connect-transforms-1.4.0.jar 설치  
```
$ cd /confluent/bin
$ ./confluent-hub install confluentinc/connect-transforms:1.4.0
```

- PATH 추가  
```
$ cd 
$ vi .bashrc
#추가
##connect-transforms
CLASSPATH=/confluent/share/confluent-hub-components/confluentinc-connect-transforms/lib/connect-transforms-1.4.0.jar
export CLASSPATH

$ . ~/.bashrc
```

- MongoDB Sink Connector 추가  

```
curl -X PUT http://zk1:8083/connectors/mongo_sink/config -H "Content-Type: application/json" -d ' {
    "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
    "tasks.max":"2",
    "topics":"ORA01.HR.REGIONS",
    "connection.uri":"mongodb://admin:mongo@172.41.41.194:27011",
    "database":"oracle",
    "collection":"REGIONS",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "value.converter.schema.registry.url": "http://sch-reg:8081",
    "key.converter.schema.registry.url": "http://sch-reg:8081",
    "transforms": "Cast",
    "transforms.Cast.type": "org.apache.kafka.connect.transforms.Cast$Value",
    "transforms.Cast.spec": "REGION_ID:int64"
}'
```

"transforms.Cast.spec": "<타입을 지정할 테이블> : <데이터 타입>"

![console](../../images/sink_type/sinktype3.png)

- Compass 접속하여 다시 확인 (이전 collection은 drop 시켜주었습니다.)  

- 검증  
```
sql> select * from HR.REGIONS ;

 REGION_ID REGION_NAME
---------- -------------------------
	13 Newyork
	 8 ENG
	 1 Europe
	 2 Americas
	 3 Asia
	 4 Middle East and Africa

6 rows selected.
```

Test by Soyoen
