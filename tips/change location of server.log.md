## Issue
- server.log의 위치를 어떻게 바꾸는지 ?  
(/confluent/etc/kafka/log4j.properties 를 참고했을 때, default 경로는 ${kafka.logs.dir} 였다.)  
- ${kafka.logs.dir} 를 따로 설정해주지 않았는데, 로그가 /confluent/logs에 쌓이고 있었다. 이곳이 default 경로인지 ?  

## Solution
```
# su - kafka 

$ export LOG_DIR=<사용자 지정 path>
$ cd /confluent/bin
$ ./kafka-server-start ../etc/kafka/server.properties
```

comment by Soyoen
