## Question
- zero-copy transfer 가 무엇인지?  
- zero-copy transfer 가 왜 빠른지?  
- SSL을 사용할 때, zero-copy transfer를 왜 사용할 수 없는지?  

## Answer
- zero-copy transfer 가 무엇인지?  
제로 카피를 사용하면 데이터가 딱 한 번 pagecache에 복사되고 (메모리에 저장되는 대신) 각 사용 시마다 재사용되며, 읽을 때마다 user-space에 복사됨.  
이를 통해 network connect의 limit에 근접한 속도로 메시지를 consume할 수 있다. 이를 통해 cache에서 데이터를 모두 처리하기 때문에 디스크에서 읽기 작업이 전혀 발생하지 않음.  
- zero-copy transfer 가 왜 빠른지?  
데이터가 암호화/해제를 위해 브로커(JVM)로 이동할 필요가 없기 때문.  
(이 과정에서 데이터 전송 속도가 약간 저하됨) SSL이 없으면 데이터를 JVM으로 전송할 필요가 없으며 캐시에서 바로 소비 되므로 속도가 빨라짐.  
- SSL을 사용할 때, zero-copy transfer를 왜 사용할 수 없는지?  
SSL을 사용하면, 브로커(JVM)가 데이터를 암호화해야 하기 때문에 데이터가 cache에서 바로 consume할 수 없기 때문임. (로직적인 문제)  

Comment by Soyoen
