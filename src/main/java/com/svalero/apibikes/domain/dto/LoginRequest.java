package com.svalero.apibikes.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class LoginRequest {
    @NotNull(message = "El campo username es obligatorio")
    private String username;

    @NotNull(message = "El campo password es obligatorio")
    private String password;
}
