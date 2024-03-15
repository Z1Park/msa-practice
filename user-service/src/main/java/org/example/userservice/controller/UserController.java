package org.example.userservice.controller;

import io.micrometer.core.annotation.Timed;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.example.userservice.dto.UserDto;
import org.example.userservice.service.UserService;
import org.example.userservice.vo.UserServiceProperties;
import org.example.userservice.vo.RequestUser;
import org.example.userservice.vo.ResponseUser;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.couchbase.CouchbaseProperties.Env;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class UserController {

	private final UserServiceProperties userServiceProperties;
	private final UserService userService;
	private final ModelMapper modelMapper = new ModelMapper();
	private final Environment environment;

	@Autowired
	public UserController(Environment environment, UserServiceProperties userServiceProperties, UserService userService) {
		this.userServiceProperties = userServiceProperties;
		this.userService = userService;
		modelMapper.getConfiguration().setMatchingStrategy(MatchingStrategies.STRICT);
		this.environment = environment;
	}


	@GetMapping("/health-check")
	@Timed(value = "users.status", longTask = true)
	public String status(HttpServletRequest request) {
		return "It's working in User-service On Port " + request.getServerPort()
				+ ", token secret = " + environment.getProperty("token.secret")
				+ ", token expiration time = " + environment.getProperty("token.expiration_time");
	}

	@GetMapping("/welcome")
	@Timed(value = "users.welcome", longTask = true)
	public String welcome() {
		return userServiceProperties.getMessage();
	}

	@GetMapping("/users")
	public List<ResponseUser> getUsers() {
		ModelMapper modelMapper = new ModelMapper();

		return userService.getUserByAll().stream()
				.map(user -> modelMapper.map(user, ResponseUser.class)).toList();
	}

	@PostMapping("/users")
	public ResponseEntity<ResponseUser> createUser(@RequestBody RequestUser requestUser) {
		UserDto userDto = modelMapper.map(requestUser, UserDto.class);
		userService.createUser(userDto);

		ResponseUser responseUser = modelMapper.map(userDto, ResponseUser.class);

		return ResponseEntity.status(HttpStatus.CREATED).body(responseUser);
	}

	@GetMapping("/users/{userId}")
	public ResponseUser getUser(@PathVariable String userId) {
		UserDto user = userService.getUserByUserId(userId);
		return modelMapper.map(user, ResponseUser.class);
	}
}
