## Question
Is it okay to set a one broker server at confluent.metrics.reporter.bootstrap.servers of server.properties ?  

Hello,I'm curious about a parameter at server.properties,Is it okay to set a one broker server at confluent.metrics.reporter.bootstrap.servers of server.properties?

Especially in the manual(https://docs.confluent.io/platform/current/kafka/metrics-reporter.html) said that I have to set 3 brokers actually I just set only one, it works.(I have one broker set up, but I can see all three at control center )

If entering all brokers is correct, why is it all visible on our C3 server?

Below are the parameters I set.

################################################

broker.id=101  
zookeeper.connect=zk1:2181,zk2:2181,zk3:2181  
listeners=PLAINTEXT://:9092  
bootstrap.servers=kafka1:9092,kafka2:9092,kafka3:9092  
log.dirs=/data/kafka-logs  
metric.reporters=io.confluent.metrics.reporter.ConfluentMetricsReporter  
confluent.metrics.reporter.bootstrap.servers=kafka1:9092  

## Answer
The client (in this case, metrics reporter) will make use of all servers irrespective of which servers are specified here for bootstrapping—this list only impacts the initial hosts used to discover the full set of servers. Since these servers are just used for the initial connection to discover the full cluster membership (which may change dynamically), this list need not contain the full set of servers. However, we recommend having full set of brokers listed to allow metrics reporter to connect to at least one broker if others are down.  I hope this helps.**  

→ 클라이언트 (이 경우 메트릭 리포터)는 부트 스트랩을 위해 여기에 지정된 서버에 관계없이 모든 서버를 사용합니다. 이 목록은 전체 서버 집합을 검색하는 데 사용되는 초기 호스트에만 영향을 줍니다. 이러한 서버는 전체 클러스터 구성원 (동적으로 변경 될 수 있음)을 검색하기위한 초기 연결에만 사용되므로 이 목록에는 전체 서버 집합이 포함될 필요가 없습니다. 그러나 다른 브로커가 다운 된 경우 하나 이상의 브로커에 연결할 수 있도록 전체 브로커 세트를 나열하는 것이 좋습니다.

추가적으로 이 파라미터는 각 브로커들(c3가 위치한 클러스터 이외 다른 클러스터의 브로커들 포함)의 메트릭 정보를 어디로 보낼건지를 의미함

따라서 c3가 위치한 클러스터의 브로커 리스트를 명시해야함.

## Summary
server.properties 의 파라미터 중, confluent.metrics.reporter.bootstrap.servers 에 한 개의 노드만 추가해도 괜찮은지에 대해 질문했습니다. 
mannual에는 3개를 추가하라고 명시되어 있는데, 1개의 노드만 추가해도 제어센터(C3)에서 모든 브로커들을 확인할 수 있었습니다.   

Comment by Soyoen Kim
