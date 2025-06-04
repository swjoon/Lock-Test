package com.backend.LockTest.domain.coupon.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "tbl_coupon")
@Builder(access = AccessLevel.PRIVATE)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	@Min(0)
	@Column(nullable = false)
	private int stock;

	public static Coupon create(final String name, final int stock) {
		return Coupon.builder()
			.name(name)
			.stock(stock)
			.build();
	}

	public void update(final String name, final int stock) {
		this.name = name;
		this.stock = stock;
	}

	public void increaseStock(final int stock) {
		this.stock += stock;
	}

	public void decreaseStock() {
		validateStockCount();
		this.stock -= 1;
	}

	private void validateStockCount() {
		if (stock < 1) {
			throw new IllegalArgumentException();
		}
	}
}
