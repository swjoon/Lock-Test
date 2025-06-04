package com.backend.LockTest.domain.coupon.repository.couponIssue;

import com.backend.LockTest.domain.coupon.entity.CouponIssue;

public interface CouponIssueRepository {

	/**
	 * 유저가 발급한 쿠폰의 일련번호를 db에 저장하는 메서드입니다.
	 *
	 * @param couponIssue 생성된 쿠폰 번호
	 * @return {@link CouponIssue}
	 * @implSpec 발급된 쿠폰번호 DB save.
	 */
	CouponIssue saveIssuedCoupon(final CouponIssue couponIssue);
}
