package com.svalero.apibikes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name="Bike")
@Table(name = "bikes")
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
    @ColumnDefault("0")
    private double latitude;
    @ColumnDefault("0")
    private double longitude;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    // Constructor adicional para aceptar solo el ID
    public Bike(long id) {
        this.id = id;
    }
}
