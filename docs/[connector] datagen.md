## What Is Datagen ?

Datagen connector는  mock data 생성을 위한 것이다.   
데모 전용 커넥터로, 운영환경에서는 사용을 권장하지 않는다.   

> **Supported Data Formats**
5.5 release부터 confluent platform은 AVRO, JSON, Protobuf 변환 패키지를 제공한다. (이전 버전에서는 AVRO만 지원)  
한편, kafka-connect-datagen과 `.` 이 포함된 protobuf 스키마 이름 간의 호환성 이슈가 있으므로 아래 페이지를 참고한다.  

https://github.com/confluentinc/kafka-connect-datagen/issues/62

> **Use a bundled schema specification** 
kafka-connect-datagen은 몇 가지 샘플 스키마(`quickstart`)를 제공하고 있다.  
해당 샘플 스키마 중 하나를 사용하려면 커넥터 기동 시 configuration 파일에 아래 매핑을 추가해주어야 한다.  
다음은 ‘users’라는 샘플 스키마를 추가하는 예시이다.  

```
...
"quickstart": "users",
...
```
다른 샘플 스키마는 아래 깃에서 확인할 수 있다.  
https://github.com/confluentinc/kafka-connect-datagen/tree/master/src/main/resources

> **Define a new schema specification**
사용자가 직접 스키마를 정의하고 데이터를 생성할 수 있다.  
내부적으로 kafka-connect-datagen은 AVRO Random Generator를 사용하므로, 커스텀하게 스키마를 작성할 때에 유일한 제약은 AVRO Random Generator와 호환되어야 한다는 것이다.  

https://github.com/confluentinc/avro-random-generator

사용자 지정의 스키마를 생성하는 과정은 다음과 같다. 

1. AVRO Random Generator와 호환되는 고유한 스키마 파일 `/path/to/your_schema.avsc` 을 생성한다.  
2. connector configuration에서 “quickstart” 필드를 대신하여 다음 파라미터를 추가한다.  

```
...
"schema.filename": "/path/to/your_schema.avsc",
"schema.keyfield": "<field representing the key>", #this is optional #use for generating key
...
```
사용자 지정 스키마는 런타임에 사용할 수 있다. 커넥터 프로세서를 다시 컴파일할 필요가 없다.  

## Requirements

운영을 위해 server.properties에서는 기본적으로 토픽 자동생성을 false로 설정한다.  
한편 해당 커넥터 기동 시, 토픽이 새로 생성되어야 하기 때문에 connect-distributed.properties 에서 토픽 자동생성을 true로 설정해 주어야 한다.  
```
$ vi connect-distributed.properties
# automatically topic create
topic.creation.enable=true
```
## Installation

- confluent-hub 사이트에서 datagen source connector 다운로드  
커넥트가 클러스터링 되어있는 경우, 모든 커넥트 서버에 다운로드 받는다.   
[confluent.io/hub/confluentinc/kafka-connect-datagen](http://confluent.io/hub/confluentinc/kafka-connect-datagen)  
로컬에서 서버의 플러그인 디렉토리로 전송한다.  

```
% scp -r confluentinc-kafka-connect-datagen-0.5.3 kafka@10.20.19.107:/kafka/plugins
```
- connector 서비스 재기동  
```
$ sudo systemctl stop kafka-connector.service
$ sudo systemctl start kafka-connector.service
```
- 확인  
```
##connect 상태 확인
$ sudo systemctl status kafka-connector.service

##플러그인 정상 등록 확인
$ curl http://rhsc2:8083/connector-plugins | jq

{
    "class": "io.confluent.kafka.connect.datagen.DatagenConnector",
    "type": "source",
    "version": "null"
  },
```
## Configuration
> **Kafka Connect Datagen Specific Parameters**
| Parameter          | Description                                                                                                          | Default |
|--------------------|----------------------------------------------------------------------------------------------------------------------|---------|
| kafka.topic        | Topic to write to                                                                                                    |         |
| max.interval       | Max interval between messages (ms)                                                                                   | 500     |
| iterations         | Number of messages to send from each task, or less than 1 for unlimited                                              | -1      |
| schema.string      | The literal JSON-encoded Avro schema to use. Cannot be set with `schema.filename` or `quickstart`                    |         |
| schema.filename    | Filename of schema to use. Cannot be set with `schema.string` or `quickstart`                                        |         |
| schema.keyfield    | Name of field to use as the message key                                                                              |         |
|                    | - 해당 파라미터를 통해 토픽에 적재되는 메시지의 key를 제어할 수 있다.                                                               |         |
|                    | - 설정될 경우, 커넥터<는 schema에서 해당 이름을 가진 필드를 찾아 해당 값을 메시지 키로 사용한다.                                          |         |
|                    | - Available type: `string`, `int`, `record`, ... etc (null 가능)                                                      |         |
|                    | - 해당 파라미터가 제공되지 않으면 키는 null이 된다.                                                                             |         |
| quickstart         | Name of `quickstart` to use. Cannot be set with `schema.string` or `schema.filename`                                 |         |


> **Configuration with Sample schema**

https://github.com/confluentinc/kafka-connect-datagen/tree/master/config

- sample schema를 이용하여 커넥터 등록  
```
$ pwd
/home/kafka
$ cd json

$ vi datagen-sample-schema-users.json 

{
  "name": "datagen-users",
  "config": {
    "connector.class": "io.confluent.kafka.connect.datagen.DatagenConnector",
    "kafka.topic": "users",
    "quickstart": "users",
    "key.converter": "org.apache.kafka.connect.storage.StringConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",
    "max.interval": 1000,
    "iterations": 10000000,
    "tasks.max": "1"
  }
}

curl -X POST -H "Content-Type: application/json" --data @datagen-sample-schema-users.json http://rhsc2:8083/connectors/
```

- 커넥터 상태 확인  
```
$ curl rhsc2:8083/connectors | jq 
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    48  100    48    0     0   1714      0 --:--:-- --:--:-- --:--:--  1777
[
  "datagen-users",
  "dev-mongo-sink-smt-tolerence"
]

$ curl rhsc2:8083/connectors/datagen-users/status  | jq 
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   157  100   157    0     0   8263      0 --:--:-- --:--:-- --:--:--  8263
{
  "name": "datagen-users",
  "connector": {
    "state": "RUNNING",
    "worker_id": "rhsc:8083"
  },
  "tasks": [
    {
      "id": 0,
      "state": "RUNNING",
      "worker_id": "rhsc:8083"
    }
  ],
  "type": "source"
}
```

- 생성된 토픽 확인
```
$ ./kafka-topics.sh --bootstrap-server rh1:9092 --list | grep users
users

$ ./kafka-console-consumer.sh --bootstrap-server rh1:9092 --topic users --from-beginning 
{"registertime":1518996663943,"userid":"User_6","regionid":"Region_1","gender":"FEMALE"}
{"registertime":1493470413119,"userid":"User_8","regionid":"Region_2","gender":"FEMALE"}
{"registertime":1512951340202,"userid":"User_6","regionid":"Region_5","gender":"MALE"}
{"registertime":1496811845610,"userid":"User_8","regionid":"Region_8","gender":"MALE"}
{"registertime":1488769317733,"userid":"User_6","regionid":"Region_9","gender":"MALE"}
...
```

> **Configuration with Customized schema**

- confluent kafka에서 같은 테스트 진행  
- 아래 깃에서 옵션 별 샘플 스키마를 확인할 수 있다.  
https://github.com/confluentinc/avro-random-generator#example-schemas  

- data를 generate할 토픽 생성  
```
$ cd /kafka/bin
$ ./kafka-topics.sh --bootstrap-server rh1:9092 --topic sy.0620.datagen.custom.regex --create
```
- customized schema 생성  
카프카 커넥터를 클러스터로 운영하고 있는 경우, 모든 서버에 같은 경로로 스키마 파일을 생성한다.  
```
$ pwd
/home/kafka
$ mkdir schemas && cd schemas 

$ vi regex.json

{
  "type": "record",
  "name": "sy_created",
  "fields":
    [
      {
        "name": "no_length_property",
        "type":
          {
            "type": "string",
            "arg.properties": {
              "regex": "[a-zA-Z]{5,15}"
            }
          }
      },
      {
        "name": "number_length_property",
        "type":
          {
            "type": "string",
            "arg.properties": {
              "regex": "[a-zA-Z]*",
              "length": 10
            }
          }
      },
      {
        "name": "min_length_property",
        "type":
          {
            "type": "string",
            "arg.properties": {
              "regex": "[a-zA-Z]{0,15}",
              "length":
                {
                  "min": 5
                }
            }
          }
      },
      {
        "name": "max_length_property",
        "type":
          {
            "type": "string",
            "arg.properties": {
              "regex": "[a-zA-Z]{5,}",
              "length":
                {
                  "max": 16
                }
            }
          }
      },
      {
        "name": "min_max_length_property",
        "type":
          {
            "type": "string",
            "arg.properties": {
              "regex": "[a-zA-Z]*",
              "length":
                {
                  "min": 5,
                  "max": 16
                }
            }
          }
      }
    ]
}
```
- customized schema를 이용하여 커넥터 등록  
```
$ pwd
/home/kafka
$ cd json

$ vi datagen-custom-schema.json 

{
  "name": "datagen-custom-schema-0620",
  "config": {
    "connector.class": "io.confluent.kafka.connect.datagen.DatagenConnector",
    "kafka.topic": "sy.0620.datagen.custom.regex",
    "schema.filename": "/home/kafka/schemas/regex.json",
    "schema.keyfield": "min_length_property",
    "key.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter": "org.apache.kafka.connect.json.JsonConverter",
    "value.converter.schemas.enable": "false",
    "max.interval": 1000,
    "iterations": 10000000,
    "tasks.max": "1"
  }
}

curl -X POST -H "Content-Type: application/json" --data @datagen-custom-schema.json http://rhsc2:8083/connectors/
```
- 커넥터 상태 확인  
```
$ curl rhsc:8083/connectors | jq
  % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100    55  100    55    0     0   3666      0 --:--:-- --:--:-- --:--:--  3666
[
  "datagen-custom-schema-0620"
]

$ curl rhsc:8083/connectors/datagen-custom-schema-0620/status | jq
% Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                 Dload  Upload   Total   Spent    Left  Speed
100   171  100   171    0     0  17100      0 --:--:-- --:--:-- --:--:-- 17100
{
  "name": "datagen-custom-schema-0620",
  "connector": {
    "state": "RUNNING",
    "worker_id": "rhsc:8083"
  },
  "tasks": [
    {
      "id": 0,
      "state": "RUNNING",
      "worker_id": "rhsc2:8083"
    }
  ],
  "type": "source"
}
```
- 메시지 컨슈밍  
```
$ ./kafka-console-consumer.sh --bootstrap-server rh1:9092 --topic sy.0620.datagen.custom.regex --from-beginning --property print.key=true
{"schema":{"type":"string","optional":false},"payload":"GqjgUNIdWyN"}	{"no_length_property":"KgZhDn","number_length_property":"kEytuaEqoi","min_length_property":"GqjgUNIdWyN","max_length_property":"RYQdHGH","min_max_length_property":"ISzcBMaLQ"}
{"schema":{"type":"string","optional":false},"payload":"HUdcU"}	{"no_length_property":"oNsgxxz","number_length_property":"MpLbwTGJjg","min_length_property":"HUdcU","max_length_property":"DSEFk","min_max_length_property":"NhGCePElC"}
{"schema":{"type":"string","optional":false},"payload":"HjXPgx"}	{"no_length_property":"VLiYM","number_length_property":"YmIYHgItNR","min_length_property":"HjXPgx","max_length_property":"bQvYqMkMogdfhtC","min_max_length_property":"kScPkp"}
{"schema":{"type":"string","optional":false},"payload":"TUMgMe"}	{"no_length_property":"YXaeLtYleZ","number_length_property":"UaEsLWaLUY","min_length_property":"TUMgMe","max_length_property":"BrxJugehKelUdLj","min_max_length_property":"kazyIa"}
....
```

## TroubleShooting

- schema를 작성할 때는 대쉬(`-`) 사용 불가, 언더바 사용할 것(`_`)  
```
##에러메시지 
ERROR Unable to parse the provided schema (io.confluent.kafka.connect.datagen.ConfigUtils:64)
org.apache.avro.SchemaParseException: Illegal character in: sy-created
```
```
##에러를 발생시킨 스키마 형태
##변경전
$ vi regex.json

{
  "type": "record",
  "name": "sy-created",
...


##변경후 
$ vi regex.json

{
  "type": "record",
  "name": "sy_created",
```
- Unable to find the schema file 오류  
  - 이슈  
    - 커스텀하게 스키마를 지정하여 datagen connector를 수행했을 때, 기동이 되지 않는 오류 
    - 에러메시지는 다음과 같다.  
  ```
  ##에러메시지 
    "state": "FAILED",
        "worker_id": "rhsc:8083",
        "trace": "org.apache.kafka.common.config.ConfigException: Unable to find the schema file\n\tat io.confluent.kafka.connect.datagen.ConfigUtils.getSchemaFromSchemaFileName(ConfigUtils.java:54)\n\tat io.confluent.kafka.connect.datagen.DatagenConnectorConfig$SchemaFileValidator.ensureValid(DatagenConnectorConfig.java:194)\n\tat org.apache.kafka.common.config.ConfigDef.parseValue(ConfigDef.java:484)\n\tat org.apache.kafka.common.config.ConfigDef.parse(ConfigDef.java:468)\n\tat org.apache.kafka.common.config.AbstractConfig.<init>(AbstractConfig.java:108)\n\tat org.apache.kafka.common.config.AbstractConfig.<init>(AbstractConfig.java:129)\n\tat io.confluent.kafka.connect.datagen.DatagenConnectorConfig.<init>(DatagenConnectorConfig.java:54)\n\tat io.confluent.kafka.connect.datagen.DatagenConnectorConfig.<init>(DatagenConnectorConfig.java:58)\n\tat io.confluent.kafka.connect.datagen.DatagenTask.start(DatagenTask.java:119)\n\tat org.apache.kafka.connect.runtime.WorkerSourceTask.execute(WorkerSourceTask.java:232)\n\tat org.apache.kafka.connect.runtime.WorkerTask.doRun(WorkerTask.java:185)\n\tat org.apache.kafka.connect.runtime.WorkerTask.run(WorkerTask.java:234)\n\tat java.util.concurrent.Executors$RunnableAdapter.call(Executors.java:511)\n\tat java.util.concurrent.FutureTask.run(FutureTask.java:266)\n\tat java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)\n\tat java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)\n\tat java.lang.Thread.run(Thread.java:748)\n"
        }
  ```

  - 해결  
    - 카프카 커넥터를 클러스터로 운영하는 경우, 모든 커넥터 서버에 커스텀 스키마를 위치시켜 주어야 한다.  
    - 그 이유는, 어떤 커넥터 서버에 태스크를 생성할지 알 수 없기 때문이다.  
    ```
    #data geneate할 토픽 생성
    $ cd /kafka/bin
    $ ./kafka-topics.sh --bootstrap-server rh1:9092 --topic sy.0620.datagen.custom.regex --create

    #customized schema 생성
    #카프카 커넥터를 클러스터로 운영하고 있는 경우, 모든 서버에 같은 경로로 스키마 파일을 생성한다.
    $ pwd
    /home/kafka
    $ mkdir schemas && cd schemas
    $ vi regex.json
    {
    "type": "record",
    "name": "sy_created",
    "fields":
        [
        {
            "name": "no_length_property",
            "type":
            {
                "type": "string",
                "arg.properties": {
                "regex": "[a-zA-Z]{5,15}"
                }
            }
        },
        {
            "name": "number_length_property",
            "type":
            {
                "type": "string",
                "arg.properties": {
                "regex": "[a-zA-Z]*",
                "length": 10
                }
            }
        },
        {
            "name": "min_length_property",
            "type":
            {
                "type": "string",
                "arg.properties": {
                "regex": "[a-zA-Z]{0,15}",
                "length":
                    {
                    "min": 5
                    }
                }
            }
        },
        {
            "name": "max_length_property",
            "type":
            {
                "type": "string",
                "arg.properties": {
                "regex": "[a-zA-Z]{5,}",
                "length":
                    {
                    "max": 16
                    }
                }
            }
        },
        {
            "name": "min_max_length_property",
            "type":
            {
                "type": "string",
                "arg.properties": {
                "regex": "[a-zA-Z]*",
                "length":
                    {
                    "min": 5,
                    "max": 16
                    }
                }
            }
        }
        ]
    }
    #customized schema를 이용하여 커넥터 등록
    $ pwd
    /home/kafka
    $ cd json
    $ vi datagen-custom-schema.json 
    {
    "name": "datagen-custom-schema-0620",
    "config": {
        "connector.class": "io.confluent.kafka.connect.datagen.DatagenConnector",
        "kafka.topic": "sy.0620.datagen.custom.regex",
        "schema.filename": "/home/kafka/schemas/regex.json",
        "schema.keyfield": "min_length_property",
        "key.converter": "org.apache.kafka.connect.json.JsonConverter",
        "value.converter": "org.apache.kafka.connect.json.JsonConverter",
        "value.converter.schemas.enable": "false",
        "max.interval": 1000,
        "iterations": 10000000,
        "tasks.max": "1"
        }
    }
    curl -X POST -H "Content-Type: application/json" --data @datagen-custom-schema.json http://rhsc2:8083/connectors/

    #커넥터 status 확인
    $ curl rhsc:8083/connectors | jq
    % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                    Dload  Upload   Total   Spent    Left  Speed
    100    55  100    55    0     0   3666      0 --:--:-- --:--:-- --:--:--  3666
    [
    "datagen-custom-schema-0620"
    ]

    $ curl rhsc:8083/connectors/datagen-custom-schema-0620/status | jq
    % Total    % Received % Xferd  Average Speed   Time    Time     Time  Current
                                    Dload  Upload   Total   Spent    Left  Speed
    100   171  100   171    0     0  17100      0 --:--:-- --:--:-- --:--:-- 17100
    {
    "name": "datagen-custom-schema-0620",
    "connector": {
        "state": "RUNNING",
        "worker_id": "rhsc:8083"
    },
    "tasks": [
        {
        "id": 0,
        "state": "RUNNING",
        "worker_id": "rhsc2:8083"
        }
    ],
    "type": "source"
    }

    #cosume
    $ ./kafka-console-consumer.sh --bootstrap-server rh1:9092 --topic sy.0620.datagen.custom.regex --from-beginning --property print.key=true
    {"schema":{"type":"string","optional":false},"payload":"GqjgUNIdWyN"}	{"no_length_property":"KgZhDn","number_length_property":"kEytuaEqoi","min_length_property":"GqjgUNIdWyN","max_length_property":"RYQdHGH","min_max_length_property":"ISzcBMaLQ"}
    {"schema":{"type":"string","optional":false},"payload":"HUdcU"}	{"no_length_property":"oNsgxxz","number_length_property":"MpLbwTGJjg","min_length_property":"HUdcU","max_length_property":"DSEFk","min_max_length_property":"NhGCePElC"}
    {"schema":{"type":"string","optional":false},"payload":"HjXPgx"}	{"no_length_property":"VLiYM","number_length_property":"YmIYHgItNR","min_length_property":"HjXPgx","max_length_property":"bQvYqMkMogdfhtC","min_max_length_property":"kScPkp"}
    {"schema":{"type":"string","optional":false},"payload":"TUMgMe"}	{"no_length_property":"YXaeLtYleZ","number_length_property":"UaEsLWaLUY","min_length_property":"TUMgMe","max_length_property":"BrxJugehKelUdLj","min_max_length_property":"kazyIa"}
    ....
    ```

## Reference
- configuration  
https://github.com/confluentinc/kafka-connect-datagen

- create new schema  
https://github.com/confluentinc/avro-random-generator