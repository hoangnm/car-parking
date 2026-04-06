package com.parking.domain.exception;

import lombok.Getter;

@Getter
public class CarNotFoundException extends ParkingException {
  private final String licensePlate;

  public CarNotFoundException(String licensePlate) {
    super(String.format("Car not found with license plate: '%s'", licensePlate));
    this.licensePlate = licensePlate;
  }
}
