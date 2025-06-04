package com.backend.LockTest.global.aop;

import java.util.concurrent.TimeUnit;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

import com.backend.LockTest.global.annotation.CustomLock;
import com.backend.LockTest.global.manager.AopTransaction;
import com.backend.LockTest.global.util.KeyGenerator;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class RedissonAspect {

	private final RedissonClient redisson;
	private final AopTransaction transaction;

	@Around("@annotation(lock)")
	public Object around(ProceedingJoinPoint joinPoint, CustomLock lock) throws Throwable {
		String key = lock.key();

		String lockKey = KeyGenerator.generateKey(joinPoint, key);

		RLock rLock = redisson.getLock(lockKey);

		boolean availableLock = false;

		try {
			availableLock = rLock.tryLock(lock.waitTime(), lock.leaseTime(), TimeUnit.MILLISECONDS);

			if (!availableLock) {
				throw new InterruptedException("Lock Timeout");
			}

			return transaction.proceed(joinPoint);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new IllegalStateException("INTERRUPTED", e);
		} finally {
			if (availableLock && rLock.isHeldByCurrentThread()) {
				rLock.unlock();
			}
		}
	}
}
