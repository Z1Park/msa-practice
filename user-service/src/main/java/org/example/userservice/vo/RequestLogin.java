package org.example.userservice.vo;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RequestLogin {

	@NotNull(message = "Email cannot be null")
	@Size(min = 2, message = "Email cannot be less than two characters")
	@Email
	private String email;

	@NotNull(message = "password cannot be null")
	@Size(min = 8, message = "Email must be equal or greater than 8 characters")
	private String password;
}
