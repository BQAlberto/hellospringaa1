package com.svalero.apibikes.domain.dto;

import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor

public class UserOutDto {

    private long id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;

}
