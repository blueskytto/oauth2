package com.dreamsecurity.auth.token;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;

@Component
public class TokenGenerator {

	public String generatorToken(int length) {
		
		String token = RandomStringUtils.randomAlphanumeric(length);
		return token;
		
	}
}
