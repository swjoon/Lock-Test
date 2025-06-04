package com.backend.LockTest.domain.coupon.repository.coupon;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.backend.LockTest.domain.coupon.entity.Coupon;

import jakarta.persistence.LockModeType;

public interface CouponJpaRepository extends JpaRepository<Coupon, Long> {

	@Lock(LockModeType.PESSIMISTIC_WRITE)
	@Query("SELECT c FROM Coupon c WHERE c.id = :id")
	Optional<Coupon> findCouponWithPessimisticLock(final @Param("id") Long id);

}
