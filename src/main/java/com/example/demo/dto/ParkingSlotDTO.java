package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParkingSlotDTO {
    private Integer id;
    private Integer carId;
    private Integer parkingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String carLicensePlate;
    private String parkingLocation;
}
