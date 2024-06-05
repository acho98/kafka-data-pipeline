## 사전 정보
- 하기 예시는 `<connector-hostname>`이 "zk1"인 서버와 `<active-connector-name>`이 "mongo_sink_demo"인 커넥터를 기반으로 표시함  
- 사용자 환경의 hostname과 설치한 plugin, 지정한 connector name에 따라 출력되는 결과는 다를 수 있음  

## 절차
- JSON pretty 출력을 위한 jq 설치  
```
$ sudo apt install jq
```
- `worker cluster ID`, `버전` 및 `git source code commit ID` 출력  
REST 요청을 처리하는 Connect worker의 버전(소스 코드의 git 커밋 ID 포함) 및 연결된 Kafka 클러스터 ID와 같은 Kafka Connect 클러스터에 대한 기본 정보를 반환한다.  

```
$ curl <connector-hostname>:8083/ | jq

$ curl -X GET <connector-hostname>:8083/ | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    94  100    94    0     0  13428      0 --:--:-- --:--:-- --:--:-- 13428
{
  "version": "6.1.1-ce",
  "commit": "bed3428d56b4e9cb",
  "kafka_cluster_id": "IouDrcY-TxKdFzO6zsU_MQ"
}
```
- connector의 worker에서 사용가능한 `plugin` 출력  
Kafka Connect 클러스터에 설치된 커넥터 플러그인 목록을 반환한다. API는 요청을 처리하는 작업자의 커넥터만 확인하므로 특히 새 커넥터 jar를 추가하는 경우 아직 미반영 되었을 수 있다.  

```
$ curl <connector-hostname>:8083/connector-plugins | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   684  100   684    0     0  85500      0 --:--:-- --:--:-- --:--:-- 85500
[
  {
    "class": "com.mongodb.kafka.connect.MongoSinkConnector",
    "type": "sink",
    "version": "1.5.1"
  },
  {
    "class": "com.mongodb.kafka.connect.MongoSourceConnector",
    "type": "source",
    "version": "1.5.1"
  },
  {
    "class": "org.apache.kafka.connect.file.FileStreamSinkConnector",
    "type": "sink",
    "version": "6.1.1-ce"
  },
  {
    "class": "org.apache.kafka.connect.file.FileStreamSourceConnector",
    "type": "source",
    "version": "6.1.1-ce"
  },
  {
    "class": "org.apache.kafka.connect.mirror.MirrorCheckpointConnector",
    "type": "source",
    "version": "1"
  },
  {
    "class": "org.apache.kafka.connect.mirror.MirrorHeartbeatConnector",
    "type": "source",
    "version": "1"
  },
  {
    "class": "org.apache.kafka.connect.mirror.MirrorSourceConnector",
    "type": "source",
    "version": "1"
  }
]
```
- worker에 등록된 connector 출력  
```
$ curl <connector-hostname>:8083/connectors

$ curl -X GET <connector-hostname>:8083/connectors

["mongo_sink_demo"]
```
- connector configuration(구성), task, connector type(유형) 출력  
```
$ curl <connector-hostname>:8083/connectors/<active-connector-name> | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   613  100   613    0     0  68111      0 --:--:-- --:--:-- --:--:-- 68111
{
  "name": "mongo_sink_demo",
  "config": {
    "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
    "value.converter.schema.registry.url": "http://sch-reg:8081",
    "database": "sytest",
    "tasks.max": "2",
    "topics": "my-test1111",
    "connection.uri": "mongodb://admin:mongo@10.20.19.74:27011",
    "name": "mongo_sink_demo",
    "collection": "mytest1111",
    "value.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter": "io.confluent.connect.avro.AvroConverter",
    "key.converter.schema.registry.url": "http://sch-reg:8081"
  },
  "tasks": [
    {
      "connector": "mongo_sink_demo",
      "task": 0
    },
    {
      "connector": "mongo_sink_demo",
      "task": 1
    }
  ],
  "type": "sink"
}
```
- connector configuration(구성) 출력  
```
$ curl <connector-hostname>:8083/connectors/<active-connector-name>/config | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   471  100   471    0     0  47100      0 --:--:-- --:--:-- --:--:-- 47100
{
  "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
  "value.converter.schema.registry.url": "http://sch-reg:8081",
  "database": "sytest",
  "tasks.max": "2",
  "topics": "my-test1111",
  "connection.uri": "mongodb://admin:mongo@10.20.19.74:27011",
  "name": "mongo_sink_demo",
  "collection": "mytest1111",
  "value.converter": "io.confluent.connect.avro.AvroConverter",
  "key.converter": "io.confluent.connect.avro.AvroConverter",
  "key.converter.schema.registry.url": "http://sch-reg:8081"
}
```
- connector `task` 출력  
task 의 개수는 `tasks.max` 파라미터에서 설정한 개수 만큼 뜬다.  
```
$ curl <connector-hostname>:8083/connectors/<active-connector-name>/tasks | jq

$ curl -X GET <connector-hostname>:8083/connectors/<active-connector-name>/tasks | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100  1179  100  1179    0     0   143k      0 --:--:-- --:--:-- --:--:--  143k
[
  {
    "id": {
      "connector": "mongo_sink_demo",
      "task": 0
    },
    "config": {
      "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
      "value.converter.schema.registry.url": "http://sch-reg:8081",
      "database": "sytest",
      "task.class": "com.mongodb.kafka.connect.sink.MongoSinkTask",
      "tasks.max": "2",
      "topics": "my-test1111",
      "connection.uri": "mongodb://admin:mongo@10.20.19.74:27011",
      "name": "mongo_sink_demo",
      "collection": "mytest1111",
      "value.converter": "io.confluent.connect.avro.AvroConverter",
      "key.converter": "io.confluent.connect.avro.AvroConverter",
      "key.converter.schema.registry.url": "http://sch-reg:8081"
    }
  },
  {
    "id": {
      "connector": "mongo_sink_demo",
      "task": 1
    },
    "config": {
      "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
      "value.converter.schema.registry.url": "http://sch-reg:8081",
      "database": "sytest",
      "task.class": "com.mongodb.kafka.connect.sink.MongoSinkTask",
      "tasks.max": "2",
      "topics": "my-test1111",
      "connection.uri": "mongodb://admin:mongo@10.20.19.74:27011",
      "name": "mongo_sink_demo",
      "collection": "mytest1111",
      "value.converter": "io.confluent.connect.avro.AvroConverter",
      "key.converter": "io.confluent.connect.avro.AvroConverter",
      "key.converter.schema.registry.url": "http://sch-reg:8081"
    }
  }
]
```
- connector의 taskId 별 status(running, failed, pause), 할당된 worker 및 실패한 경우 오류 정보 출력  
```
$ curl <connector-hostname>:8083/connectors/<active-connector-name>/tasks/<taskid>/status | jq

$ curl -X GET <connector-hostname>:8083/connectors/<active-connector-name>/tasks/<taskId>/status | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    48  100    48    0     0   6000      0 --:--:-- --:--:-- --:--:--  6857
{
  "id": 0,
  "state": "RUNNING",
  "worker_id": "zk1:8083"
}
```
- connector `status`(상태) 출력  
`UNASSIGNED`: connector/task가 아직 worker에게 할당되지 않은 상태이다.  
`RUNNING`: connector/task가 실행중이다.  
`PAUSED`: connector/task가 관리상 일시중지되었다.  
`FAILED` : connector/task이 실패했다. (status에 report되는 예외가 발생한 경우)  
```
$ curl <connector-hostname>:8083/connectors/<active-connector-name>/status | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   202  100   202    0     0  28857      0 --:--:-- --:--:-- --:--:-- 28857
{
  "name": "mongo_sink_demo",
  "connector": {
    "state": "RUNNING",
    "worker_id": "zk1:8083"
  },
  "tasks": [
    {
      "id": 0,
      "state": "RUNNING",
      "worker_id": "zk1:8083"
    },
    {
      "id": 1,
      "state": "RUNNING",
      "worker_id": "zk1:8083"
    }
  ],
  "type": "sink"
}
```
- 특정 connector가 사용 중인 topic 목록 출력  
```
$ curl <connector-hostname>:8083/connectors/<active-connector-name>/topics | jq

$ curl -X GET <connector-hostname>:8083/connectors/<active-connector-name>/topics | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    33  100    33    0     0   8250      0 --:--:-- --:--:-- --:--:--  8250
{
  "mongo_sink_demo": {
    "topics": [
      "sytest"
    ]
  }
}
```
- 특정 connector가 사용 중인 topic 목록을 비우도록 요청  
해당 명령어는 커넥터가 생성된 이후 혹은 active topcis가 마지막으로 재설정된 이후에 사용했던 토픽의 이름을 초기화한다. 이때 active topic 이란, connector가 현재 작업(consumer가 read or producer가 write) 중인 topic을 의미한다.  
```
$ curl -X PUT <connector-hostname>:8083/connectors/<active-connector-name>/topics/reset | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
  0     0    0     0    0     0      0      0 --:--:-- --:--:-- --:--:--     0
```

## 서비스 기동 옵션
- 새로운 connector 등록  
```
$ curl -X PUT http://<connector-hostname>:8083/connectors/<active-connector-name>/config -H "Content-Type: application/json" -d ' {
      "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
      "tasks.max":"2",
      "topics":"<kafka-topic>",
      "connection.uri":"mongodb://admin:mongo@<mongodbIP>:27011",
      "database":"<database-name>",
      "collection":"<collection-name>",
      "key.converter": "io.confluent.connect.avro.AvroConverter",
      "value.converter": "io.confluent.connect.avro.AvroConverter",
      "value.converter.schema.registry.url": "http://<schema-registry-hostname>:8081",
      "key.converter.schema.registry.url": "http://<schema-registry-hostname>:8081"
}'
```
- connector 재시작  
해당 명령 성공 시 아무것도 출력되지 않으며, connector를 다시 시작해도 task가 다시 시작되는 것은 아니다.  
```
$ curl -X POST <connector-hostname>:8083/connectors/<active-connector-name>/restart
```
- includeTasks 매개변수는 connector 와 task 인스턴스를 모두 포함하여 재시작할 경우 `includeTasks=true`, connector만 포함할 경우 `includeTasks=false` 로 지정한다. 이때 기본값인 false는 이전 버전과 동일한 동작을 유지한다.   
onlyFailed 매개변수는 status가 FAILED 인 인스턴스만 재시작할 경우 `onlyFailed=true` , status에 상관없이 모든 인스턴스를 재시작할 경우 `onlyFailed=false` 로 지정한다. 이때 기본값인 false는 이전 버전과 동일한 동작을 유지한다.  
```
$ curl -X POST <connector-hostname>:8083/connectors/<active-connector-name>/restart?includeTasks=<true|false>&onlyFailed=<true|false>
```
- taskId별 재시작  
일반적으로 task가 실패했을 경우 taskId별로 재시작을 할 수 있다.  
```
$ curl -X POST <connector-hostname>:8083/connectors/<active-connector-name>/tasks/<taskId>/restart
```
- connector 중지  
해당 명령 성공 시 아무것도 출력되지 않으며, 이 명령은 커넥터가 상호 작용하는 시스템이 유지 관리를 위해 일시적으로 서비스를 중단해야 하는 경우 유용하다.  
```
$ curl -X PUT <connector-hostname>:8083/connectors/<active-connector-name>/pause
```
- status를 확인하면 다음과 같다.  
```
$ curl -X GET zk1:8083/connectors/mongo-test/status | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   197  100   197    0     0  65666      0 --:--:-- --:--:-- --:--:-- 65666
{
  "name": "mongo_sink_demo",
  "connector": {
    "state": "PAUSED",
    "worker_id": "zk1:8083"
  },
  "tasks": [
    {
      "id": 0,
      "state": "PAUSED",
      "worker_id": "zk1:8083"
    },
    {
      "id": 1,
      "state": "PAUSED",
      "worker_id": "zk1:8083"
    }
  ],
  "type": "sink"
}
```
- 일시 중지된 connector 재개  
```
$ curl -X PUT <connector-hostname>:8083/connectors/<active-connector-name>/resume

$ curl -X GET zk1:8083/connectors/mongo-test/status | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   200  100   200    0     0  33333      0 --:--:-- --:--:-- --:--:-- 33333
{
  "name": "mongo_sink_demo",
  "connector": {
    "state": "RUNNING",
    "worker_id": "zk1:8083"
  },
  "tasks": [
    {
      "id": 0,
      "state": "RUNNING",
      "worker_id": "zk1:8083"
    },
    {
      "id": 1,
      "state": "RUNNING",
      "worker_id": "zk1:8083"
    }
  ],
  "type": "sink"
}
```
- connector config 업데이트  
1. 업데이트를 하는 첫번째 방법은 다음과 같다.  
    1) 현재 커넥터의 config 복사  
    ```
    $ curl http://<connect:port>/connectors/<connector_name>/config | jq . > connector.json
    ```
    2) config 변경사항 수정과 적용  
    ```
    $ curl -X PUT http://<connect:port>/connectors/<connector_name>/config -d @connector.json -H "Content-Type: application/json"
    ```
2. 위의 방법이 번거롭다면, config를 변경하고자 하는 connector이름은 그대로 둔 채, 파라미터들만 바꾸어 다시 재등록할 수도 있다.  
    1) 기존 mongo_sink_demo 커넥터의 config  
    ```
    $ curl -X GET zk1:8083/connectors/mongo-test-2 | jq
    % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                    Dload  Upload   Total   Spent    Left  Speed
    100   594  100   594    0     0   145k      0 --:--:-- --:--:-- --:--:--  145k
    {
    "name": "mongo_sink_demo",
    "config": {
        "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
        "value.converter.schema.registry.url": "http://sch-reg:8081",
        "database": "test3",
        "tasks.max": "2",
        "topics": "test1119",
        "connection.uri": "mongodb://admin:mongo@10.20.19.74:27011",
        "name": "mongo-test-2",
        "collection": "sytest3",
        "value.converter": "io.confluent.connect.avro.AvroConverter",
        "key.converter": "io.confluent.connect.avro.AvroConverter",
        "key.converter.schema.registry.url": "http://sch-reg:8081"
    },
    "tasks": [
        {
        "connector": "mongo_sink_demo",
        "task": 0
        },
        {
        "connector": "mongo_sink_demo",
        "task": 1
        }
    ],
    "type": "sink"
    }
    ```
    2) tasks.max 수를 3으로, topics를 sytest1119 로 변경  
    ```
    $ curl -X PUT http://zk1:8083/connectors/mongo_sink_demo/config -H "Content-Type: application/json" -d ' {
      "connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
      "tasks.max":"3",
      "topics":"sytest1119",
      "connection.uri":"mongodb://admin:mongo@10.20.19.74:27011",
      "database":"test3",
      "collection":"sytest3",
      "key.converter": "io.confluent.connect.avro.AvroConverter",
      "value.converter": "io.confluent.connect.avro.AvroConverter",
      "value.converter.schema.registry.url": "http://sch-reg:8081",
      "key.converter.schema.registry.url": "http://sch-reg:8081"
    }'
    ```
    3) 확인  
    ```
    $ curl -X GET zk1:8083/connectors/mongo-test-2 | jq
    % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                    Dload  Upload   Total   Spent    Left  Speed
    100   634  100   634    0     0   123k      0 --:--:-- --:--:-- --:--:--  154k
    {
    "name": "mongo_sink_demo",
    "config": {
        "connector.class": "com.mongodb.kafka.connect.MongoSinkConnector",
        "value.converter.schema.registry.url": "http://sch-reg:8081",
        "database": "test3",
        "tasks.max": "3",
        "topics": "sytest1119",
        "connection.uri": "mongodb://admin:mongo@10.20.19.74:27011",
        "name": "mongo-test-2",
        "collection": "sytest3",
        "value.converter": "io.confluent.connect.avro.AvroConverter",
        "key.converter": "io.confluent.connect.avro.AvroConverter",
        "key.converter.schema.registry.url": "http://sch-reg:8081"
    },
    "tasks": [
        {
        "connector": "mongo-test-2",
        "task": 0
        },
        {
        "connector": "mongo-test-2",
        "task": 1
        },
        {
        "connector": "mongo-test-2",
        "task": 2
        }
    ],
    "type": "sink"
    }
    ```
- connector config 유효성 검사  
```
$ curl -X PUT <connector-hostname>:8083/connector-plugins/<connector_type>/config/validate -d @connector.json -H "Content-Type: application/json" | jq

$ curl -X PUT \
-H "Content-Type: application/json" \
--data '{
"connector.class":"com.mongodb.kafka.connect.MongoSinkConnector",
"tasks.max":"3",
"topics":"mytopic",
"connection.uri": "mongodb://admin:mongo@10.10.10.1:27011",
"database":"test3",
"collection":"sytest3",
"key.converter": "io.confluent.connect.avro.AvroConverter",
"value.converter": "io.confluent.connect.avro.AvroConverter",
"value.converter.schema.registry.url": "http://sch-reg:8081",
"key.converter.schema.registry.url": "http://sch-reg:8081"
}' \
http://zk1:8083/connector-plugins/MongoSinkConnector/config/validate | jq

% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100 28707    0 28253  100   454   192k   3174 --:--:-- --:--:-- --:--:--  196k
{
  "name": "com.mongodb.kafka.connect.MongoSinkConnector",
  "error_count": 0,
  "groups": [
    "Common",
    "Transforms",
    "Predicates",
    "Error Handling",
    "Connection",
    "Overrides",
    "Namespace",
    "Namespace mapping",
    "Writes",
    "Post Processing",
    "Id Strategies",
    "Errors",
    "Change Data Capture"
  ],
  "configs": [
    {
      "definition": {
        "name": "name",
        "type": "STRING",
        "required": true,
        "default_value": null,
        "importance": "HIGH",
        "documentation": "Globally unique name to use for this connector.",
        "group": "Common",
        "width": "MEDIUM",
        "display_name": "Connector name",
        "dependents": [],
        "order": 1
      },
 ...
 #이런식으로 각 value마다 필요값들이 출력된다
```

- connector 삭제  
```
$ curl -X DELETE <connector-hostname>:8083/connectors/<active-connector-name>
```

comment by soyoen




