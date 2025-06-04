package com.backend.LockTest.domain.coupon.util;

import java.time.LocalDate;
import java.util.UUID;

import com.backend.LockTest.domain.coupon.constant.CouponConstant;

public class UuidUtil {

	public static String getCouponUUID(final Long id) {
		return CouponConstant.COUPON_UUID.formatted(id, createUUID());
	}

	private static String createUUID() {
		return LocalDate.now().toString() + UUID.randomUUID().toString().substring(0, 7);
	}
}
