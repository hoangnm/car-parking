package com.parking.application.service;

import com.parking.application.port.in.DepartCarUseCase;
import com.parking.application.port.in.ParkCarUseCase;
import com.parking.application.port.out.CarRepositoryPort;
import com.parking.application.port.out.ParkingRepositoryPort;
import com.parking.application.port.out.ParkingSessionRepositoryPort;
import com.parking.domain.exception.CarNotFoundException;
import com.parking.domain.exception.ParkingNotFoundException;
import com.parking.domain.model.Car;
import com.parking.domain.model.Parking;
import com.parking.domain.model.ParkingSession;
import com.parking.dto.CarDTO;
import com.parking.dto.ParkingSessionDTO;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CarService implements ParkCarUseCase, DepartCarUseCase {
  private final CarRepositoryPort carRepositoryPort;
  private final ParkingRepositoryPort parkingRepositoryPort;
  private final ParkingSessionRepositoryPort parkingSessionRepositoryPort;

  @Transactional
  public ParkingSessionDTO parkCar(CarDTO carDTO, Integer parkingId) {
    log.atDebug()
        .setMessage("Attempting to park car")
        .addKeyValue("action", "park_car")
        .addKeyValue("licensePlate", carDTO.getLicensePlate())
        .addKeyValue("parkingId", parkingId)
        .log();

    Parking parking =
        parkingRepositoryPort
            .findById(parkingId)
            .orElseThrow(() -> new ParkingNotFoundException(parkingId));

    // Fetch active sessions and assign to the domain model
    LocalDateTime currentTime = LocalDateTime.now();
    List<ParkingSession> activeSessions =
        parkingSessionRepositoryPort.findActiveSessions(parkingId, currentTime);
    parking.setActiveSessions(activeSessions);

    // Create or get car
    Car car = carRepositoryPort.findByLicensePlate(carDTO.getLicensePlate());
    if (car == null) {
      log.atDebug()
          .setMessage("Creating new car entry")
          .addKeyValue("action", "create_car")
          .addKeyValue("licensePlate", carDTO.getLicensePlate())
          .log();
      car = new Car();
      BeanUtils.copyProperties(carDTO, car);
      car = carRepositoryPort.save(car);
    } else {
      log.atDebug()
          .setMessage("Car found")
          .addKeyValue("id", car.getId())
          .addKeyValue("action", "check_active_slot")
          .log();
    }

    // Create parking slot via domain model
    ParkingSession parkingSession = parking.parkCar(car);

    parkingSession = parkingSessionRepositoryPort.save(parkingSession);

    log.atDebug()
        .setMessage("Car parked successfully")
        .addKeyValue("action", "park_complete")
        .addKeyValue("sessionId", parkingSession.getId())
        .addKeyValue("carId", car.getId())
        .addKeyValue("parkingId", parking.getId())
        .log();

    // Convert to DTO
    ParkingSessionDTO parkingSessionDTO = new ParkingSessionDTO();
    parkingSessionDTO.setId(parkingSession.getId());
    parkingSessionDTO.setCarId(car.getId());
    parkingSessionDTO.setParkingId(parking.getId());
    parkingSessionDTO.setStartTime(parkingSession.getStartTime());
    parkingSessionDTO.setCarLicensePlate(car.getLicensePlate());
    parkingSessionDTO.setParkingLocation(parking.getLocation());

    return parkingSessionDTO;
  }

  @Transactional
  public Car registerCarDeparture(Integer parkingId, String licensePlate) {
    log.atDebug()
        .setMessage("Attempting to remove car")
        .addKeyValue("action", "remove_car")
        .addKeyValue("licensePlate", licensePlate)
        .addKeyValue("parkingId", parkingId)
        .log();

    Car car = carRepositoryPort.findByLicensePlate(licensePlate);
    if (car == null) {
      log.atWarn()
          .setMessage("Car not found")
          .addKeyValue("action", "remove_car")
          .addKeyValue("licensePlate", licensePlate)
          .addKeyValue("status", "not_found")
          .log();
      throw new CarNotFoundException(licensePlate);
    }

    Parking parking =
        parkingRepositoryPort
            .findById(parkingId)
            .orElseThrow(() -> new ParkingNotFoundException(parkingId));

    LocalDateTime currentTime = LocalDateTime.now();
    List<ParkingSession> activeSessions =
        parkingSessionRepositoryPort.findActiveSessions(parkingId, currentTime);
    parking.setActiveSessions(activeSessions);

    ParkingSession endedSession = parking.departCar(car);

    parkingSessionRepositoryPort.save(endedSession);

    log.atDebug()
        .setMessage("Car removed successfully")
        .addKeyValue("action", "remove_complete")
        .addKeyValue("carId", car.getId())
        .addKeyValue("sessionId", endedSession.getId())
        .addKeyValue("parkingId", parkingId)
        .log();

    return car;
  }

  public Car getCarByLicensePlate(String licensePlate) {
    log.atDebug()
        .setMessage("Fetching car")
        .addKeyValue("action", "get_car")
        .addKeyValue("licensePlate", licensePlate)
        .log();
    return carRepositoryPort.findByLicensePlate(licensePlate);
  }
}
