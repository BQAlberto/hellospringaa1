package com.svalero.apibikes.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkShopInDto {

    @NotNull(message = "El campo name es obligatorio")
    private String name;
    @NotNull(message = "El campo address es obligatorio")
    private String address;
    private String phone;
    private String email;
}
