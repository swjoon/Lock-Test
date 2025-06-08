package com.backend.LockTest.domain.coupon.service;

import com.backend.LockTest.domain.coupon.dto.request.CreateCouponDto;
import com.backend.LockTest.domain.coupon.dto.response.GetCouponDto;

public interface CouponService {

	Long createCoupon(final CreateCouponDto requestDto);

	GetCouponDto getCoupon(final Long id);

	void issueCoupon(final Long id);

	void issueCouponWithPessimisticLock(final Long id);

	void issueCouponWithRedissonLock(final Long id);

	void issueCouponWithLuaScript(final Long id);

}
