package com.svalero.apibikes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BikeRegistrationDto {

    @NotNull(message = "El campo brand es obligatorio")
    private String brand;
    @NotNull(message = "El campo model es obligatorio")
    private String model;
    private LocalDate releaseDate;
    private String color;
    private double latitude;
    private double longitude;
}
