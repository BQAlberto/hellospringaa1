package com.svalero.apibikes.domain.dto;

import com.svalero.apibikes.domain.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class BikeOutDto {

    private long id;
    private String brand;
    private String model;
    private long userId;
    private double latitude;
    private double longitude;
    private String color;
}
