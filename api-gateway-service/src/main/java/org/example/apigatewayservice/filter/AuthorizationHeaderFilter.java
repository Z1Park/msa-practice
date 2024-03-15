package org.example.apigatewayservice.filter;

import io.jsonwebtoken.Jwts;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.example.apigatewayservice.config.JwtProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthorizationHeaderFilter extends AbstractGatewayFilterFactory<AuthorizationHeaderFilter.Config> {

	private final Environment environment;

	public static class Config {
	}

	@Autowired
	public AuthorizationHeaderFilter(Environment environment) {
		super(Config.class);
		this.environment = environment;
	}

	@Override
	public GatewayFilter apply(Config config) {
		return ((exchange, chain) -> {
			ServerHttpRequest request = exchange.getRequest();

			if (!request.getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
				return onError(exchange, "no authorization header", HttpStatus.UNAUTHORIZED);
			}
			String authorizationHeader = request.getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
			String jwt = authorizationHeader.replace("Bearer ", "");

			if (!isJwtValid(jwt)) {
				return onError(exchange, "JWT token is not valid", HttpStatus.UNAUTHORIZED);
			}

			return chain.filter(exchange);
		});
	}

	private boolean isJwtValid(String jwt) {
		String subject = null;

		try {
			subject = Jwts.parser().setSigningKey(environment.getProperty("token.secret"))
					.parseClaimsJws(jwt).getBody().getSubject();
		} catch (Exception e) {
			return false;
		}

		return !StringUtil.isNullOrEmpty(subject);
	}

	private Mono<Void> onError(ServerWebExchange exchange, String message, HttpStatus httpStatus) {
		ServerHttpResponse response = exchange.getResponse();
		response.setStatusCode(httpStatus);

		log.error(message);
		return response.setComplete();
	}
}
