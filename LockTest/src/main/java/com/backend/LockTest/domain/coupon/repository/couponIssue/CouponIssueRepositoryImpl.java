package com.backend.LockTest.domain.coupon.repository.couponIssue;

import org.springframework.stereotype.Service;

import com.backend.LockTest.domain.coupon.entity.CouponIssue;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponIssueRepositoryImpl implements CouponIssueRepository {

	private final CouponIssueJpaRepository couponIssueJpaRepository;

	@Override
	public CouponIssue saveIssuedCoupon(CouponIssue couponIssue) {
		return couponIssueJpaRepository.save(couponIssue);
	}
}
