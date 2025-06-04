package com.backend.LockTest.domain.coupon.repository.coupon;

import java.util.Optional;

import com.backend.LockTest.domain.coupon.entity.Coupon;

public interface CouponRepository {

	Coupon saveCoupon(final Coupon coupon);

	Optional<Coupon> findById(final Long id);

	Optional<Coupon> findByIdWithPessimisticLock(final Long id);
}
