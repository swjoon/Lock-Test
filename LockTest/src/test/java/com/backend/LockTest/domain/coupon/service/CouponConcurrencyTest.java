package com.backend.LockTest.domain.coupon.service;

import static org.assertj.core.api.AssertionsForClassTypes.*;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.backend.LockTest.domain.coupon.entity.Coupon;
import com.backend.LockTest.domain.coupon.repository.coupon.CouponRepository;
import com.backend.LockTest.domain.coupon.repository.couponIssue.CouponIssueJpaRepository;

import jakarta.persistence.EntityManager;

@SpringBootTest
@ActiveProfiles("test")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class CouponConcurrencyTest {

	@Autowired
	private EntityManager em;

	@Autowired
	private CouponService couponService;

	@Autowired
	private CouponRepository couponRepository;

	@Autowired
	private CouponIssueJpaRepository couponIssueJpaRepository;

	private static final Deque<Long> ID_LIST = new ConcurrentLinkedDeque<>();

	private static final int THREAD_COUNT = 100;
	private static final int STOCK = 120;

	@BeforeAll
	@Commit
	@Transactional
	void setUpCoupons() {
		for (int i = 0; i < 5; i++) {
			ID_LIST.add(couponRepository.saveCoupon(Coupon.create("coupon" + i, STOCK)).getId());
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

		System.out.println("[No Lock] 티켓 발급 소요시간: " + (endTime - startTime) + " ms " + " couponId: " + couponId);

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

		System.out.println("[PessimisticLock] 티켓 발급 소요시간: " + (endTime - startTime) + " ms" + " couponId: " + couponId);

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

		System.out.println("[Redisson] 티켓 발급 소요시간: " + (endTime - startTime) + " ms" + " couponId: " + couponId);

		assertThat(couponRepository.findById(couponId).orElseThrow().getStock()).isEqualTo(STOCK - THREAD_COUNT);
		assertThat(couponIssueJpaRepository.countByCouponId(couponId)).isEqualTo(THREAD_COUNT);
	}
}
