package com.backend.LockTest.global.manager;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopTransaction {

	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
		return joinPoint.proceed();
	}
}
