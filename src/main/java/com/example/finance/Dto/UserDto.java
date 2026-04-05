package com.example.finance.Dto;

import com.example.finance.entity.Role;
import com.example.finance.entity.UserStatus;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserDto {
    private String username;

    @NotNull(message = "Role is required")
    private Role role;
    private UserStatus status;
}