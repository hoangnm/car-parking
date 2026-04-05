package com.parking.domain.model;

import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ParkingSession {
  private Integer id;
  private Car car;
  private Parking parking;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
}
