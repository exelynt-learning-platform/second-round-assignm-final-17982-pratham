package com.backend.productcart.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LoginRequest {
	@NotBlank
	private String username;

	@NotBlank
	private String password;
}
