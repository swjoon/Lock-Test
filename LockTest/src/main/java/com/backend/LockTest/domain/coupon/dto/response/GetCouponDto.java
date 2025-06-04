package com.backend.LockTest.domain.coupon.dto.response;

import com.backend.LockTest.domain.coupon.entity.Coupon;

public record GetCouponDto(
	String name,
	int amount
) {
	public static GetCouponDto from(final Coupon coupon) {
		return new GetCouponDto(
			coupon.getName(),
			coupon.getStock()
		);
	}
}
