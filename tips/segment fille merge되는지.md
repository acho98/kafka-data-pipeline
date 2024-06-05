
## Question
- segment file이 merge도 되는지?  

## Answer
- 세그먼트 파일은 append only로 디자인되어 있다.  
- cleanup.policy=delete일 경우 retention 기간이 지난 old segment는 삭제된다.  
- cleanup.policy=compact일 경우 모든 세그먼트는 최신 세그먼트로 복사된 해당 오프셋을 가지며 이전 세그먼트는 삭제된다.  
- 세그먼트는 merge 기능은 없다.  