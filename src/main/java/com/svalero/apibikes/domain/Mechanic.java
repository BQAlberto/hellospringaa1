package com.svalero.apibikes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Mechanic")
@Table(name = "mechanics")
public class Mechanic {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column
    private String phone;
    @Column
    private String specialization;
    @OneToMany(mappedBy = "mechanic")
    private List<RepairOrder> repairOrders;

    // Constructor adicional para aceptar solo el ID
    public Mechanic(long id) {
        this.id = id;
    }
}

