package com.parking.domain.model;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Getter
@Setter
public class ParkingSlot {
    private Integer id;
    private Car car;
    private Parking parking;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
