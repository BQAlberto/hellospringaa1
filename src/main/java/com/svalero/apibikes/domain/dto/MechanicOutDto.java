package com.svalero.apibikes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MechanicOutDto {

    private long id;
    private String name;
    private String surname;
    private String phone;
    private String specialization;
}
