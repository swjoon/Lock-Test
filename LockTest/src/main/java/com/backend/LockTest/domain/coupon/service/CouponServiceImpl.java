package com.backend.LockTest.domain.coupon.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.LockTest.domain.coupon.dto.request.CreateCouponDto;
import com.backend.LockTest.domain.coupon.dto.response.GetCouponDto;
import com.backend.LockTest.domain.coupon.entity.Coupon;
import com.backend.LockTest.domain.coupon.entity.CouponIssue;
import com.backend.LockTest.domain.coupon.repository.coupon.CouponRepository;
import com.backend.LockTest.domain.coupon.repository.couponIssue.CouponIssueRepository;
import com.backend.LockTest.domain.coupon.util.UuidUtil;
import com.backend.LockTest.global.annotation.CustomLock;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponServiceImpl implements CouponService {

	private final CouponRepository couponRepository;
	private final CouponIssueRepository couponIssueRepository;

	@Override
	public Long createCoupon(final CreateCouponDto requestDto) {

		Coupon coupon = couponRepository.saveCoupon(Coupon.create(requestDto.name(), requestDto.stock()));

		return coupon.getId();
	}

	@Override
	public GetCouponDto getCoupon(Long id) {

		Coupon coupon = couponRepository.findById(id).orElseThrow();

		return GetCouponDto.from(coupon);
	}

	@Override
	@Transactional
	public void issueCoupon(final Long id) {

		Coupon coupon = couponRepository.findById(id).orElseThrow();

		coupon.decreaseStock();

		CouponIssue issuedCoupon = CouponIssue.create(id, UuidUtil.getCouponUUID(id));

		couponIssueRepository.saveIssuedCoupon(issuedCoupon);
	}

	@Override
	@Transactional
	public void issueCouponWithPessimisticLock(final Long id) {

		Coupon coupon = couponRepository.findByIdWithPessimisticLock(id).orElseThrow();

		coupon.decreaseStock();

		CouponIssue issuedCoupon = CouponIssue.create(id, UuidUtil.getCouponUUID(id));

		couponIssueRepository.saveIssuedCoupon(issuedCoupon);
	}

	@Override
	@Transactional
	@CustomLock(key = "'Coupon:' + #id", waitTime = 10000, leaseTime = 4000)
	public void issueCouponWithRedissonLock(final Long id) {
		Coupon coupon = couponRepository.findById(id).orElseThrow();

		coupon.decreaseStock();

		CouponIssue issuedCoupon = CouponIssue.create(id, UuidUtil.getCouponUUID(id));

		couponIssueRepository.saveIssuedCoupon(issuedCoupon);
	}

}
