package com.example.finance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Entity
@Data
public class Users {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    @Email(message = "Invalid email format")
    @Column(unique = true)
    private String email;

    @NotNull(message = "Role is required")
    @Enumerated(EnumType.STRING)
    private Role role;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = false)
    private boolean deleted = false;
}
