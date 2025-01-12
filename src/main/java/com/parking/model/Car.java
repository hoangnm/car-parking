package com.parking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Car {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String licensePlate;
    private String brand;
    private String model;
    private String color;
    
    @OneToMany(mappedBy = "car")
    private List<ParkingSlot> parkingSlots;
}
