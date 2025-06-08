package com.backend.LockTest.domain.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.backend.LockTest.domain.coupon.constant.CouponConstant;
import com.backend.LockTest.domain.coupon.dto.request.CreateCouponDto;
import com.backend.LockTest.domain.coupon.repository.coupon.CouponRepository;
import com.backend.LockTest.domain.coupon.repository.couponIssue.CouponIssueJpaRepository;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouponConcurrencyTest {

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	@Autowired
	private CouponService couponService;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CouponIssueJpaRepository couponIssueJpaRepository;

	private static final Deque<Long> ID_LIST = new ConcurrentLinkedDeque<>();
	private static final Deque<String> REDIS_ID_LIST = new ConcurrentLinkedDeque<>();

	private static final int THREAD_COUNT = 1000;
	private static final int STOCK = 12000;

	@BeforeAll
	@Commit
	@Transactional
	void setUpCoupons() {
		for (int i = 0; i < 5; i++) {
			Long id = couponService.createCoupon(CreateCouponDto.builder()
				.name("coupon" + i)
				.stock(STOCK)
				.build());

			ID_LIST.add(id);
			REDIS_ID_LIST.add(CouponConstant.LUA_KEY.formatted(id));
		}
	}

	@AfterAll
	void clearUp() {
		while (!REDIS_ID_LIST.isEmpty()) {
			redisTemplate.delete(REDIS_ID_LIST.poll());
		}
	}

	@Test
	void issuedCoupon_TEST() throws Exception {

		Long couponId = ID_LIST.poll();

		long startTime = System.currentTimeMillis();

		try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
			var latch = new CountDownLatch(THREAD_COUNT);
			for (int i = 0; i < THREAD_COUNT; i++) {
				exec.submit(() -> {
					try {
						couponService.issueCoupon(couponId);
					} finally {
						latch.countDown();
					}
				});
			}
			latch.await();
		}

		long endTime = System.currentTimeMillis();

		System.out.println("[No Lock] 쿠폰 발급 소요시간: " + (endTime - startTime) + " ms " + " couponId: " + couponId);

		assertThat(couponRepository.findById(couponId).orElseThrow().getStock()).isEqualTo(STOCK - THREAD_COUNT);
		assertThat(couponIssueJpaRepository.countByCouponId(couponId)).isEqualTo(THREAD_COUNT);
	}

	@Test
	void issuedCouponWithPessimisticLock_TEST() throws Exception {
		Long couponId = ID_LIST.poll();

		long startTime = System.currentTimeMillis();

		try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
			var latch = new CountDownLatch(THREAD_COUNT);
			for (int i = 0; i < THREAD_COUNT; i++) {
				exec.submit(() -> {
					try {
						couponService.issueCouponWithPessimisticLock(couponId);
					} finally {
						latch.countDown();
					}
				});
			}
			latch.await();
		}

		long endTime = System.currentTimeMillis();

		System.out.println("[PessimisticLock] 쿠폰 발급 소요시간: " + (endTime - startTime) + " ms" + " couponId: " + couponId);

		assertThat(couponRepository.findById(couponId).orElseThrow().getStock()).isEqualTo(STOCK - THREAD_COUNT);
		assertThat(couponIssueJpaRepository.countByCouponId(couponId)).isEqualTo(THREAD_COUNT);
	}

	@Test
	void issuedCouponWithRedisson_TEST() throws Exception {
		Long couponId = ID_LIST.poll();

		long startTime = System.currentTimeMillis();

		try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
			var latch = new CountDownLatch(THREAD_COUNT);
			for (int i = 0; i < THREAD_COUNT; i++) {
				exec.submit(() -> {
					try {
						couponService.issueCouponWithRedissonLock(couponId);
					} finally {
						latch.countDown();
					}
				});
			}
			latch.await();
		}

		long endTime = System.currentTimeMillis();

		System.out.println("[Redisson] 쿠폰 발급 소요시간: " + (endTime - startTime) + " ms" + " couponId: " + couponId);

		assertThat(couponRepository.findById(couponId).orElseThrow().getStock()).isEqualTo(STOCK - THREAD_COUNT);
		assertThat(couponIssueJpaRepository.countByCouponId(couponId)).isEqualTo(THREAD_COUNT);
	}

	@Test
	void issuedCouponWithLuaScript_TEST() throws Exception {
		Long couponId = ID_LIST.poll();
		String redisKey = CouponConstant.LUA_KEY.formatted(couponId);

		long startTime = System.currentTimeMillis();

		try (var exec = Executors.newVirtualThreadPerTaskExecutor()) {
			var latch = new CountDownLatch(THREAD_COUNT);
			for (int i = 0; i < THREAD_COUNT; i++) {
				exec.submit(() -> {
					try {
						couponService.issueCouponWithLuaScript(couponId);
					} finally {
						latch.countDown();
					}
				});
			}
			latch.await();
		}
		long endTime = System.currentTimeMillis();

		System.out.println("[Redis Lua] 쿠폰 발급 소요시간: " + (endTime - startTime) + " ms" + " couponId: " + couponId);

		assertThat(redisTemplate.opsForValue().get(redisKey)).isEqualTo(STOCK - THREAD_COUNT);
		assertThat(couponIssueJpaRepository.countByCouponId(couponId)).isEqualTo(THREAD_COUNT);
	}
}
