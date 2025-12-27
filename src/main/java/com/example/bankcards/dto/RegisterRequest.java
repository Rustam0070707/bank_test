package com.example.bankcards.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequest {
    @NotNull(message = "Username is required")
    @NotBlank(message = "Username is required")
    private String username;
    @NotNull(message = "Password is required")
    @NotBlank(message = "Password is required")
    private String password;
}
