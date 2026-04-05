package com.parking.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "parking_slot")
@Getter
@Setter
public class ParkingSlotEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @ManyToOne
    @JoinColumn(name = "car_id")
    private CarEntity car;
    
    @ManyToOne
    @JoinColumn(name = "parking_id")
    private ParkingEntity parking;
    
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
