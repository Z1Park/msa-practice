package org.example.userservice.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Data;
import org.example.userservice.vo.ResponseOrder;

@Data
public class UserDto {

	private String email;
	private String name;
	private String password;
	private String userId;
	private LocalDateTime createdAt;

	private String encryptedPassword;
	private List<ResponseOrder> orders;
}
