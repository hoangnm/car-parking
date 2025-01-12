package com.parking.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class ParkingSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "car_id")
    private Car car;
    
    @ManyToOne
    @JoinColumn(name = "parking_id")
    private Parking parking;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
