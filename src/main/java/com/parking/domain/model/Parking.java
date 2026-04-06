package com.parking.domain.model;

import com.parking.exception.ParkingException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parking {
  private Integer id;
  private String location;
  private Integer capacity;
  private Double latitude;
  private Double longitude;

  private List<ParkingSession> parkingSessions = new ArrayList<>();

  public ParkingSession parkCar(Car car, long activeSessions, boolean isCarAlreadyParked) {
    if (activeSessions >= this.capacity) {
      throw new ParkingException("Parking is full");
    }
    if (isCarAlreadyParked) {
      throw new ParkingException(
          "Car with license plate "
              + car.getLicensePlate()
              + " is already parked in this parking lot");
    }

    ParkingSession parkingSession = new ParkingSession();
    parkingSession.setCar(car);
    parkingSession.setParking(this);
    parkingSession.setStartTime(LocalDateTime.now());

    return parkingSession;
  }
}
