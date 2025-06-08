package com.backend.LockTest.domain.coupon.service;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.backend.LockTest.domain.coupon.constant.CouponConstant;
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

	private final RedisTemplate<String, Object> redisTemplate;

	private final CouponRepository couponRepository;
	private final CouponIssueRepository couponIssueRepository;

	private final CouponRedisService couponRedisService;

	@Override
	public Long createCoupon(final CreateCouponDto requestDto) {

		Coupon coupon = couponRepository.saveCoupon(Coupon.create(requestDto.name(), requestDto.stock()));

		redisTemplate.opsForValue()
			.set(CouponConstant.LUA_KEY.formatted(coupon.getId()), coupon.getStock());

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
	@CustomLock(key = "'Coupon:' + #id", waitTime = 14_000, leaseTime = 3_000)
	public void issueCouponWithRedissonLock(final Long id) {
		Coupon coupon = couponRepository.findById(id).orElseThrow();

		coupon.decreaseStock();

		CouponIssue issuedCoupon = CouponIssue.create(id, UuidUtil.getCouponUUID(id));

		couponIssueRepository.saveIssuedCoupon(issuedCoupon);
	}

	@Override
	@Transactional
	public void issueCouponWithLuaScript(final Long id) {

		if (!couponRedisService.issueCouponWithLua(id)) {
			throw new IllegalStateException("품절");
		}

		CouponIssue issuedCoupon = CouponIssue.create(id, UuidUtil.getCouponUUID(id));

		couponIssueRepository.saveIssuedCoupon(issuedCoupon);

	}

}
