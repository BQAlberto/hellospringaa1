package com.svalero.apibikes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "RepairOrder")
@Table(name = "repair_orders")
public class RepairOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "bike_id", nullable = false)
    private Bike bike;
    @ManyToOne
    @JoinColumn(name = "mechanic_id", nullable = false)
    private Mechanic mechanic;
    @ManyToOne
    @JoinColumn(name = "workshop_id", nullable = false)
    private WorkShop workShop;
    @Column(nullable = false)
    private LocalDate repairDate;
    @Column(nullable = false)
    private double cost;
    @Column(length = 500)
    private String description;
}

