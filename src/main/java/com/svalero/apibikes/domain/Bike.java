package com.svalero.apibikes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="bikes")
public class Bike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column
    private String brand;
    @Column
    private String model;
    @Column(name ="release_date")
    private LocalDate releaseDate;
    @Column(name="registration_date")
    private LocalDate registrationDate;
    @Column
    private String color;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;
}
