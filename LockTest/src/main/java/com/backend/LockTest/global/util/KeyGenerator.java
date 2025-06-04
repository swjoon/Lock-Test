package com.backend.LockTest.global.util;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import jakarta.validation.constraints.NotNull;

public class KeyGenerator {

	private static final ExpressionParser PARSER = new SpelExpressionParser();

	private static final Map<String, Expression> EXPRESSION_MAP = new ConcurrentHashMap<>();

	public static String generateKey(final ProceedingJoinPoint joinPoint, @NotNull final String spelKey) {

		Expression expr = EXPRESSION_MAP.computeIfAbsent(spelKey, PARSER::parseExpression);

		MethodSignature signature = (MethodSignature)joinPoint.getSignature();
		Object[] args = joinPoint.getArgs();
		String[] paramNames = signature.getParameterNames();

		StandardEvaluationContext context = new StandardEvaluationContext();

		for (int i = 0; i < paramNames.length && i < args.length; i++) {
			context.setVariable(paramNames[i], args[i]);
		}

		Object value = expr.getValue(context);

		if (value == null) {
			throw new IllegalArgumentException("Lock key cannot be null");
		}

		return value.toString();
	}

}
