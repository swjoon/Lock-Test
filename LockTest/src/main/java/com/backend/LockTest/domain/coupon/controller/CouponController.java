package com.backend.LockTest.domain.coupon.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.backend.LockTest.domain.coupon.dto.request.CreateCouponDto;
import com.backend.LockTest.domain.coupon.dto.response.GetCouponDto;
import com.backend.LockTest.domain.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/coupon")
public class CouponController {

	private final CouponService couponService;

	@PostMapping
	public Long create(@RequestBody final CreateCouponDto requestDto) {

		return couponService.createCoupon(requestDto);
	}

	@GetMapping("/{id}")
	public GetCouponDto get(@PathVariable final Long id) {

		return couponService.getCoupon(id);
	}

	@PostMapping("/{id}/issue")
	public void issueCoupon(@PathVariable final Long id) {

		couponService.issueCoupon(id);
	}
}
