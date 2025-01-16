package com.svalero.apibikes.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MechanicInDto {

    @NotNull(message = "El campo name es obligatorio")
    private String name;
    @NotNull(message = "El campo surname es obligatorio")
    private String surname;
    private String phone;
    private String specialization;
}
