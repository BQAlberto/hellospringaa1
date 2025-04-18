package com.svalero.apibikes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkShopOutDto {

    private long id;
    private String name;
    private String address;
    private String phone;
    private String email;
}
