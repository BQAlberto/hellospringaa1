package com.svalero.apibikes.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairOrderInDto {

    @NotNull(message = "El campo bikeId es obligatorio")
    private long bikeId;
    @NotNull(message = "El campo mechanicId es obligatorio")
    private long mechanicId;
    @NotNull(message = "El campo workShopId es obligatorio")
    private long workShopId;
    @NotNull(message = "El campo repairDate es obligatorio")
    private LocalDate repairDate;
    private double cost;
    private String description;
}
