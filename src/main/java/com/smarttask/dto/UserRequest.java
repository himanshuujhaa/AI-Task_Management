package com.smarttask.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import static com.smarttask.constants.Constants.*;

@Getter
@Setter
public class UserRequest {
    @NotBlank(message = NAME_REQUIRED)
    private String name;

    @Email(message = INVALID_EMAIL)
    @NotBlank(message = EMAIL_REQUIRED)
    private String email;

    @NotBlank(message = PASSWORD_REQUIRED)
    private String password;
}
