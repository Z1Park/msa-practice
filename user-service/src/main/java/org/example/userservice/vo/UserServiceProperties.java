package org.example.userservice.vo;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class UserServiceProperties {

	@Value("${greeting.message}")
	private String message;

	@Value("${token.secret}")
	private String tokenSecret;

	@Value("${token.expiration_time}")
	private Integer expirationTime;
}
