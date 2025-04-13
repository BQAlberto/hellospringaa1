package com.svalero.apibikes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInDto {

    @NotNull(message = "El campo username es obligatorio")
    private String username;
    @NotNull(message = "El campo password es obligatorio")
    private String password;
    @NotNull(message = "El campo name es obligatorio")
    private String name;
    @NotNull(message = "El campo surname es obligatorio")
    private String surname;
    private String email;
}
