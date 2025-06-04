package com.backend.LockTest.domain.coupon.repository.couponIssue;

import org.springframework.data.jpa.repository.JpaRepository;

import com.backend.LockTest.domain.coupon.entity.CouponIssue;

public interface CouponIssueJpaRepository extends JpaRepository<CouponIssue, Long> {

	Long countByCouponId(final Long couponId);

}
