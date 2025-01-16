package com.svalero.apibikes.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserInDto {

    @NotNull(message = "El campo name es obligatorio")
    private String name;
    @NotNull(message = "El campo surname es obligatorio")
    private String surname;
    private String email;
}
