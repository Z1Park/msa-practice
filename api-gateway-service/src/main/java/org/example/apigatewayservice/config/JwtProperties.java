package org.example.apigatewayservice.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Getter
@Component
public class JwtProperties {

	@Value("${token.secret}")
	private String secret;
}
