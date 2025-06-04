
# Concurreny Test

#### Local 환경 & 단일 서버 기준

### Thread_Count = 10
- 비관적락 (PessimisticLock) : 262 ms 
- 분산락 (Redisson) + DB Insert : 288 ms


### Thread_Count = 100
- 비관적락 (PessimisticLock) : 1287 ms
- 분산락 (Redisson) + DB Insert : 4748 ms