package org.example.userservice.service;

import io.micrometer.tracing.Tracer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.userservice.dto.UserDto;
import org.example.userservice.repository.UserEntity;
import org.example.userservice.repository.UserRepository;
import org.example.userservice.vo.ResponseOrder;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.circuitbreaker.CircuitBreaker;
import org.springframework.cloud.client.circuitbreaker.CircuitBreakerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final ModelMapper modelMapper = new ModelMapper();
//	private final Environment environment;
//	private final RestTemplate restTemplate;
	private final OrderServiceClient orderServiceClient;
	private final CircuitBreakerFactory circuitBreakerFactory;


	@Autowired
	public UserServiceImpl(UserRepository userRepository,
			BCryptPasswordEncoder passwordEncoder,
			Environment environment,
//			RestTemplate restTemplate,
			OrderServiceClient orderServiceClient,
			CircuitBreakerFactory circuitBreakerFactory) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
//		this.environment = environment;
//		this.restTemplate = restTemplate;
		this.orderServiceClient = orderServiceClient;
		this.circuitBreakerFactory = circuitBreakerFactory;

		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserEntity userEntity = userRepository.findByEmail(username);

		if (userEntity == null) {
			throw new UsernameNotFoundException(username);
		}
		return User.builder()
				.username(userEntity.getEmail())
				.password(userEntity.getEncryptedPassword())
				.build();
	}

	@Override
	public UserDto createUser(UserDto userDto) {
		userDto.setUserId(UUID.randomUUID().toString());

		UserEntity userEntity = modelMapper.map(userDto, UserEntity.class);
		userEntity.setEncryptedPassword(passwordEncoder.encode(userDto.getPassword()));

		userRepository.save(userEntity);

		return modelMapper.map(userEntity, UserDto.class);
	}

	@Override
	public UserDto getUserByUserId(String userId) {
		UserEntity userEntity = userRepository.findByUserId(userId);

		if (userEntity == null) {
			throw new UsernameNotFoundException("User not found");
		}
		UserDto userDto = new ModelMapper().map(userEntity, UserDto.class);

		/* Using RestTemplate */
//		@SuppressWarnings("ConstantConditions")
//		String orderUrl = String.format(environment.getProperty("order_service.url"), userId);
//		ResponseEntity<List<ResponseOrder>> orderListResponse =
//				restTemplate.exchange(orderUrl, HttpMethod.GET, null,
//						new ParameterizedTypeReference<List<ResponseOrder>>() {
//				});
//		List<ResponseOrder> orderList = orderListResponse.getBody();

		/* Using FeignClient + Error Decoder */
//		List<ResponseOrder> orderList = orderServiceClient.getOrders(userId);

		/* Using CircuitBreaker */
		log.info("Before call orders microservice");
		CircuitBreaker circuitbreaker = circuitBreakerFactory.create("circuitbreaker");
		List<ResponseOrder> orderList = circuitbreaker.run(() -> orderServiceClient.getOrders(userId),
				throwable -> new ArrayList<>());
		log.info("After called orders microservice");

		userDto.setOrders(orderList);

		return userDto;
	}

	@Override
	public List<UserEntity> getUserByAll() {
		return userRepository.findAll();
	}

	@Override
	public UserDto getUserDetailsByEmail(String email) {
		UserEntity userEntity = userRepository.findByEmail(email);
		if (userEntity == null) {
			throw new UsernameNotFoundException(email);
		}
		return modelMapper.map(userEntity, UserDto.class);
	}
}
