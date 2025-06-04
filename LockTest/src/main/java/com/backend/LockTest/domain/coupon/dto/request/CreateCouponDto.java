package com.backend.LockTest.domain.coupon.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record CreateCouponDto(

	@NotBlank(message = "쿠폰명은 필수 입력값 입니다.")
	String name,

	@Positive(message = "수량은 0보다 큰 값 입니다.")
	int stock

) {
}