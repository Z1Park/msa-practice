package org.example.userservice.security;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import org.example.userservice.service.UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.util.matcher.IpAddressMatcher;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

//	public static final IpAddressMatcher ipAddressMatcher = new IpAddressMatcher("172.18.0.5");
//	public static final IpAddressMatcher ipAddressMatcher = new IpAddressMatcher("192.168.0.4");
	public static final IpAddressMatcher ipAddressMatcher = new IpAddressMatcher("10.19.238.70");

	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final UserService userService;
	private final Environment environment;

	public SecurityConfig(BCryptPasswordEncoder bCryptPasswordEncoder, UserService userService,
			Environment environment) {
		this.bCryptPasswordEncoder = bCryptPasswordEncoder;
		this.userService = userService;
		this.environment = environment;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		return http.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(authorize -> authorize
						.requestMatchers("/actuator/**").permitAll()
						.requestMatchers("/**").access(this::hasIpAddress))
				.addFilter(getAuthenticationFilter())
				.headers(header -> header.frameOptions(FrameOptionsConfig::disable))
				.build();
	}

	private AuthorizationDecision hasIpAddress(Supplier<Authentication> authentication, RequestAuthorizationContext object) {
		return new AuthorizationDecision(ipAddressMatcher.matches(object.getRequest()));
	}

	@Bean
	public AuthenticationFilter getAuthenticationFilter() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userService);
		authenticationProvider.setPasswordEncoder(bCryptPasswordEncoder);

		AuthenticationManager authenticationManager = new ProviderManager(authenticationProvider);
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, environment);
		authenticationFilter.setAuthenticationManager(authenticationManager);
		return authenticationFilter;
	}
}
