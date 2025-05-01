package com.svalero.apibikes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkShopInDto {

    @NotNull(message = "El campo name es obligatorio")
    private String name;
    @NotNull(message = "El campo address es obligatorio")
    private String address;
    @NotBlank(message = "El teléfono es obligatorio")
    private String phone;
    @NotBlank(message = "El correo electrónico es obligatorio")
    @Email(message = "El correo electrónico no tiene un formato válido")
    private String email;
}
