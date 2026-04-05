package com.parking.dto;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class ParkingSessionDTO {
  private Integer id;
  private Integer carId;
  private Integer parkingId;
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private String carLicensePlate;
  private String parkingLocation;
}
