package com.svalero.apibikes.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "WorkShop")
@Table(name = "workshops")
public class WorkShop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String address;
    @Column
    private String phone;
    @Column
    private String email;
    @OneToMany(mappedBy = "workShop")
    private List<RepairOrder> repairOrders;

    // Constructor adicional para aceptar solo el ID
    public WorkShop(long id) {
        this.id = id;
    }
}
