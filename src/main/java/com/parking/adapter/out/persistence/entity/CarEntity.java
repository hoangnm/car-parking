package com.parking.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "car")
@Getter
@Setter
public class CarEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    
    @OneToMany(mappedBy = "car")
    private List<ParkingSlotEntity> parkingSlots = new ArrayList<>();
}
