package com.parking.application.service;

import com.parking.adapter.out.persistence.entity.CarEntity;
import com.parking.adapter.out.persistence.entity.ParkingEntity;
import com.parking.adapter.out.persistence.entity.ParkingSessionEntity;
import com.parking.application.port.in.DepartCarUseCase;
import com.parking.application.port.in.ParkCarUseCase;
import com.parking.domain.exception.ResourceNotFoundException;
import com.parking.domain.model.Car;
import com.parking.domain.model.Parking;
import com.parking.domain.model.ParkingSession;
import com.parking.dto.CarDTO;
import com.parking.dto.ParkingSessionDTO;
import com.parking.mapper.DomainMapper;
import com.parking.repository.CarRepository;
import com.parking.repository.ParkingRepository;
import com.parking.repository.ParkingSessionRepository;
import java.time.LocalDateTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class CarService implements ParkCarUseCase, DepartCarUseCase {
  private final CarRepository carRepository;
  private final ParkingRepository parkingRepository;
  private final ParkingSessionRepository parkingSessionRepository;

  public CarService(
      CarRepository carRepository,
      ParkingRepository parkingRepository,
      ParkingSessionRepository parkingSessionRepository) {
    this.carRepository = carRepository;
    this.parkingRepository = parkingRepository;
    this.parkingSessionRepository = parkingSessionRepository;
  }

  @SuppressWarnings("null")
  @Transactional
  public ParkingSessionDTO parkCar(CarDTO carDTO, @NonNull Integer parkingId) {
    log.atDebug()
        .setMessage("Attempting to park car")
        .addKeyValue("action", "park_car")
        .addKeyValue("licensePlate", carDTO.getLicensePlate())
        .addKeyValue("parkingId", parkingId)
        .log();

    ParkingEntity parkingEntity =
        parkingRepository
            .findById(parkingId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Parking not found with id: " + parkingId));
    Parking parking = DomainMapper.toDomain(parkingEntity);

    // Fetch active sessions and assign to the domain model
    LocalDateTime currentTime = LocalDateTime.now();
    java.util.List<ParkingSessionEntity> activeSessionEntities =
        parkingSessionRepository.findActiveSessions(parkingId, currentTime);
    java.util.List<ParkingSession> activeSessions =
        activeSessionEntities.stream()
            .map(DomainMapper::toDomain)
            .collect(java.util.stream.Collectors.toList());
    parking.setActiveSessions(activeSessions);

    // Create or get car
    CarEntity carEntity = carRepository.findByLicensePlate(carDTO.getLicensePlate());
    Car car;
    if (carEntity == null) {
      log.atDebug()
          .setMessage("Creating new car entry")
          .addKeyValue("action", "create_car")
          .addKeyValue("licensePlate", carDTO.getLicensePlate())
          .log();
      car = new Car();
      BeanUtils.copyProperties(carDTO, car);
      carEntity = DomainMapper.toEntity(car);
      carEntity = carRepository.save(carEntity);
      car = DomainMapper.toDomain(carEntity);
    } else {
      car = DomainMapper.toDomain(carEntity);
      log.atDebug()
          .setMessage("Car found")
          .addKeyValue("id", car.getId())
          .addKeyValue("action", "check_active_slot")
          .log();
    }

    // Create parking slot via domain model
    ParkingSession parkingSession = parking.parkCar(car);

    ParkingSessionEntity parkingSessionEntity = DomainMapper.toEntity(parkingSession);
    parkingSessionEntity = parkingSessionRepository.save(parkingSessionEntity);
    parkingSession = DomainMapper.toDomain(parkingSessionEntity);

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

  @SuppressWarnings("null")
  @Transactional
  public Car registerCarDeparture(@NonNull Integer parkingId, String licensePlate) {
    log.atDebug()
        .setMessage("Attempting to remove car")
        .addKeyValue("action", "remove_car")
        .addKeyValue("licensePlate", licensePlate)
        .addKeyValue("parkingId", parkingId)
        .log();

    CarEntity carEntity = carRepository.findByLicensePlate(licensePlate);
    if (carEntity == null) {
      log.atWarn()
          .setMessage("Car not found")
          .addKeyValue("action", "remove_car")
          .addKeyValue("licensePlate", licensePlate)
          .addKeyValue("status", "not_found")
          .log();
      throw new ResourceNotFoundException("Car not found with license plate: " + licensePlate);
    }
    Car car = DomainMapper.toDomain(carEntity);

    ParkingEntity parkingEntity =
        parkingRepository
            .findById(parkingId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Parking not found with id: " + parkingId));
    Parking parking = DomainMapper.toDomain(parkingEntity);

    LocalDateTime currentTime = LocalDateTime.now();
    java.util.List<ParkingSessionEntity> activeSessionEntities =
        parkingSessionRepository.findActiveSessions(parkingId, currentTime);
    java.util.List<ParkingSession> activeSessions =
        activeSessionEntities.stream()
            .map(DomainMapper::toDomain)
            .collect(java.util.stream.Collectors.toList());
    parking.setActiveSessions(activeSessions);

    ParkingSession endedSession = parking.departCar(car);

    ParkingSessionEntity endedSessionEntity = DomainMapper.toEntity(endedSession);
    parkingSessionRepository.save(endedSessionEntity);

    log.atDebug()
        .setMessage("Car removed successfully")
        .addKeyValue("action", "remove_complete")
        .addKeyValue("carId", car.getId())
        .addKeyValue("sessionId", endedSessionEntity.getId())
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
    CarEntity carEntity = carRepository.findByLicensePlate(licensePlate);
    return DomainMapper.toDomain(carEntity);
  }
}
