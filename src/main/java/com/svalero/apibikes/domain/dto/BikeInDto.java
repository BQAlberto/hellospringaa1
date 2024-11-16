package com.svalero.apibikes.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeInDto {

    @NotNull(message = "El campo brand es obligatorio")
    private String brand;
    @NotNull(message = "El campo model es obligatorio")
    private String model;
    private LocalDate releaseDate;
    private String color;

}
