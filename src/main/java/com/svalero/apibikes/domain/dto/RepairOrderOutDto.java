package com.svalero.apibikes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RepairOrderOutDto {

    private long id;
    private long bikeId;
    private long mechanicId;
    private long workShopId;
    private LocalDate repairDate;
    private double cost;
    private String description;
}
