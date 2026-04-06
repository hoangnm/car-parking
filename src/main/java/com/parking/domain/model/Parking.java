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

  private List<ParkingSession> activeSessions = new ArrayList<>();

  public ParkingSession parkCar(Car car) {
    if (this.activeSessions.size() >= this.capacity) {
      throw new ParkingException("Parking is full");
    }

    boolean isCarAlreadyParked =
        this.activeSessions.stream()
            .anyMatch(session -> session.getCar().getLicensePlate().equals(car.getLicensePlate()));

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

    this.activeSessions.add(parkingSession);
    return parkingSession;
  }

  public ParkingSession departCar(Car car) {
    ParkingSession sessionToDepart =
        this.activeSessions.stream()
            .filter(session -> session.getCar().getLicensePlate().equals(car.getLicensePlate()))
            .findFirst()
            .orElseThrow(
                () ->
                    new ParkingException(
                        "Car with license plate "
                            + car.getLicensePlate()
                            + " is not parked in this parking lot"));

    sessionToDepart.endSession();
    return sessionToDepart;
  }
}
