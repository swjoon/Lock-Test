package com.backend.LockTest.domain.coupon.constant;

public class CouponConstant {
	public static final String COUPON_UUID = "COUPON:%d:%s";
	public static final String LUA_KEY = "coupon:%d:stock";
	public static final String REDISSON_KEY = "lock:coupon:%s";
}
