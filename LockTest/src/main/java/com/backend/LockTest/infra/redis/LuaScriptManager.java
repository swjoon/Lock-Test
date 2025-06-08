package com.backend.LockTest.infra.redis;

import java.nio.charset.StandardCharsets;

import org.redisson.api.RedissonClient;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class LuaScriptManager {

	private static final String SCRIPT_PATH = "redis/issue_coupon.lua";

	private final RedissonClient redisson;

	@Getter
	private String issueCouponSha;

	@PostConstruct
	public void loadScript() throws Exception {
		byte[] bytes = new ClassPathResource(SCRIPT_PATH)
			.getContentAsByteArray();

		String script = new String(bytes, StandardCharsets.UTF_8);

		this.issueCouponSha = redisson.getScript()
			.scriptLoad(script);

	}

	public String ensureSha() {
		if (!redisson.getScript().scriptExists(issueCouponSha).getFirst()) {
			loadScriptSilently();
		}
		return issueCouponSha;
	}

	private void loadScriptSilently() {
		try {
			loadScript();
		} catch (Exception ignored) {
		}
	}

}
