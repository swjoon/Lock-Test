package com.backend.LockTest.domain.coupon.service;

import java.util.List;

import org.redisson.api.RScript;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Service;

import com.backend.LockTest.domain.coupon.constant.CouponConstant;
import com.backend.LockTest.infra.redis.LuaScriptManager;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CouponRedisService {

	private final RedissonClient redisson;
	private final LuaScriptManager luaScriptManager;

	public boolean issueCouponWithLua(final Long couponId) {

		String key = CouponConstant.LUA_KEY.formatted(couponId);

		String sha = luaScriptManager.ensureSha();

		Long result = redisson.getScript().evalSha(
			RScript.Mode.READ_WRITE,
			sha,
			RScript.ReturnType.INTEGER,
			List.of(key),
			"1"
		);

		if (result == null)
			throw new IllegalStateException("Redis error");
		if (result == -2)
			throw new IllegalStateException("재고 키 없음");
		return result != -1;
	}
}
