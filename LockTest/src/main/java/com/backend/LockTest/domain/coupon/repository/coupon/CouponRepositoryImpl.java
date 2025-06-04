package com.backend.LockTest.domain.coupon.repository.coupon;

import java.util.Optional;

import org.springframework.stereotype.Repository;

import com.backend.LockTest.domain.coupon.entity.Coupon;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CouponRepositoryImpl implements CouponRepository {

	private final CouponJpaRepository couponJpaRepository;

	@Override
	public Coupon saveCoupon(final Coupon coupon) {
		return couponJpaRepository.save(coupon);
	}

	@Override
	public Optional<Coupon> findById(final Long id) {
		return couponJpaRepository.findById(id);
	}

	@Override
	public Optional<Coupon> findByIdWithPessimisticLock(final Long id) {
		return couponJpaRepository.findCouponWithPessimisticLock(id);
	}
}
