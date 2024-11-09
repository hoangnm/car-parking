package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String location;
    private Integer capacity;
    
    @OneToMany(mappedBy = "parking")
    private List<ParkingSlot> parkingSlots;
}
