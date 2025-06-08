
# Concurreny Test

#### Local 환경 & 단일 서버 기준

### Thread_Count = 10
- 비관적락 (PessimisticLock) : 262 ms 
- 분산락 (Redisson) + DB Insert : 288 ms (waitTime = 10 s, leaseTime = 3 s)
- Lua Script(Redis) + DB Insert : 50 ms

### Thread_Count = 100
- 비관적락 (PessimisticLock) : 1287 ms
- 분산락 (Redisson) + DB Insert : 4748 ms (waitTime = 10 s, leaseTime = 3 s)
- Lua Script(Redis) + DB Insert : 166 ms

### Thread_Count = 1000
- 비관적락 (PessimisticLock) : 8231 ms
- 분산락 (Redisson) + DB Insert : 11291 ms (waitTime = 12 s, leaseTime = 3 s)
- Lua Script(Redis) + DB Insert : 479 ms