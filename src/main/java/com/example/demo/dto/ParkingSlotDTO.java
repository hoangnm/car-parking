package com.example.demo.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ParkingSlotDTO {
    private Long carId;
    private Long parkingId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
