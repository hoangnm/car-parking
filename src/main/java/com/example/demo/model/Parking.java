package com.example.demo.model;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
public class Parking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String location;
    private Integer capacity;
    private Double latitude;
    private Double longitude;
    
    @OneToMany(mappedBy = "parking")
    private List<ParkingSlot> parkingSlots;
}
