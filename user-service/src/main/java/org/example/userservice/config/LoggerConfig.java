package org.example.userservice.config;

import feign.Logger;
import feign.Logger.Level;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LoggerConfig {

	@Bean
	public Logger.Level feignLoggerLevel() {
		return Level.FULL;
	}
}
