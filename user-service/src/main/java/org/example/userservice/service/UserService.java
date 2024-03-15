package org.example.userservice.service;

import java.util.List;
import org.example.userservice.dto.UserDto;
import org.example.userservice.repository.UserEntity;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
	UserDto createUser(UserDto userDto);

	UserDto getUserByUserId(String userId);

	List<UserEntity> getUserByAll();

	UserDto getUserDetailsByEmail(String email);
}
