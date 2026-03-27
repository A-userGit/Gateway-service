package com.shop.gatewayservice.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record UserDataDto(
    @NotNull
    String name,
    @NotNull
    String surname,
    @Email
    String email,
    @Pattern(regexp = "^(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[012])-\\d{4}$",
        message = "Invalid date format. The expected format is dd-MM-yyyy")
    String birthDate,
    @NotNull
    String password) {

}
