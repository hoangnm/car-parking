package com.parking.domain.exception;

import lombok.Getter;

@Getter
public class ParkingNotFoundException extends ParkingException {
  private final Integer parkingId;

  public ParkingNotFoundException(Integer parkingId) {
    super(String.format("Parking not found with id: '%d'", parkingId));
    this.parkingId = parkingId;
  }
}
