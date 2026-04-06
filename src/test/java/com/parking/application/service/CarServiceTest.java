package com.parking.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.parking.adapter.out.persistence.entity.CarEntity;
import com.parking.adapter.out.persistence.entity.ParkingEntity;
import com.parking.adapter.out.persistence.entity.ParkingSessionEntity;
import com.parking.domain.exception.ParkingException;
import com.parking.domain.exception.ResourceNotFoundException;
import com.parking.domain.model.Car;
import com.parking.dto.CarDTO;
import com.parking.dto.ParkingSessionDTO;
import com.parking.repository.CarRepository;
import com.parking.repository.ParkingRepository;
import com.parking.repository.ParkingSessionRepository;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

  @Mock private CarRepository carRepository;

  @Mock private ParkingRepository parkingRepository;

  @Mock private ParkingSessionRepository parkingSessionRepository;

  private CarService carService;

  private CarEntity testCarEntity;
  private ParkingEntity testParkingEntity;
  private ParkingSessionEntity testParkingSessionEntity;
  private CarDTO testCarDTO;

  @BeforeEach
  void setUp() {
    carService = new CarService(carRepository, parkingRepository, parkingSessionRepository);

    // Setup test data
    testCarEntity = new CarEntity();
    testCarEntity.setId(1);
    testCarEntity.setLicensePlate("ABC123");

    testParkingEntity = new ParkingEntity();
    testParkingEntity.setId(1);
    testParkingEntity.setCapacity(100);
    testParkingEntity.setLocation("Test Location");

    testParkingSessionEntity = new ParkingSessionEntity();
    testParkingSessionEntity.setId(1);
    testParkingSessionEntity.setCar(testCarEntity);
    testParkingSessionEntity.setParking(testParkingEntity);
    testParkingSessionEntity.setStartTime(LocalDateTime.now());

    testCarDTO = new CarDTO();
    testCarDTO.setLicensePlate("ABC123");
  }

  @Test
  void parkCar_Success() {
    // Arrange
    when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
    when(parkingSessionRepository.findActiveSessions(anyInt(), any()))
        .thenReturn(java.util.Collections.emptyList());
    when(carRepository.findByLicensePlate("ABC123")).thenReturn(null);
    when(carRepository.save(any(CarEntity.class))).thenReturn(testCarEntity);
    when(parkingSessionRepository.save(any(ParkingSessionEntity.class)))
        .thenReturn(testParkingSessionEntity);

    // Act
    ParkingSessionDTO result = carService.parkCar(testCarDTO, 1);

    // Assert
    assertNotNull(result);
    assertEquals(testParkingSessionEntity.getId(), result.getId());
    assertEquals(testCarEntity.getId(), result.getCarId());
    assertEquals(testParkingEntity.getId(), result.getParkingId());
    verify(parkingSessionRepository).save(any(ParkingSessionEntity.class));
  }

  @Test
  void parkCar_ParkingFull() {
    // Arrange
    when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
    java.util.List<ParkingSessionEntity> fullSessions = new java.util.ArrayList<>();
    for (int i = 0; i < 100; i++) fullSessions.add(new ParkingSessionEntity());
    when(parkingSessionRepository.findActiveSessions(anyInt(), any())).thenReturn(fullSessions);

    // Act & Assert
    assertThrows(ParkingException.class, () -> carService.parkCar(testCarDTO, 1));
  }

  @Test
  void parkCar_CarAlreadyParked() {
    // Arrange
    when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
    when(parkingSessionRepository.findActiveSessions(anyInt(), any()))
        .thenReturn(java.util.List.of(testParkingSessionEntity));
    when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCarEntity);

    // Act & Assert
    assertThrows(ParkingException.class, () -> carService.parkCar(testCarDTO, 1));
  }

  @Test
  void removeCarFromParking_Success() {
    // Arrange
    when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCarEntity);
    when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
    when(parkingSessionRepository.findActiveSessions(anyInt(), any()))
        .thenReturn(java.util.List.of(testParkingSessionEntity));
    when(parkingSessionRepository.save(any(ParkingSessionEntity.class)))
        .thenReturn(testParkingSessionEntity);

    // Act
    Car result = carService.registerCarDeparture(1, "ABC123");

    // Assert
    assertNotNull(result);
    assertEquals(testCarEntity.getId(), result.getId());
    assertEquals(testCarEntity.getLicensePlate(), result.getLicensePlate());
    verify(parkingSessionRepository).save(any(ParkingSessionEntity.class));
  }

  @Test
  void removeCarFromParking_CarNotFound() {
    // Arrange
    when(carRepository.findByLicensePlate("ABC123")).thenReturn(null);

    // Act & Assert
    assertThrows(
        ResourceNotFoundException.class, () -> carService.registerCarDeparture(1, "ABC123"));
  }

  @Test
  void removeCarFromParking_CarNotParked() {
    // Arrange
    when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCarEntity);
    when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
    when(parkingSessionRepository.findActiveSessions(anyInt(), any()))
        .thenReturn(java.util.Collections.emptyList());

    // Act & Assert
    assertThrows(ParkingException.class, () -> carService.registerCarDeparture(1, "ABC123"));
  }

  @Test
  void getCarByLicensePlate_Success() {
    // Arrange
    when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCarEntity);

    // Act
    Car result = carService.getCarByLicensePlate("ABC123");

    // Assert
    assertNotNull(result);
    assertEquals(testCarEntity.getId(), result.getId());
    assertEquals(testCarEntity.getLicensePlate(), result.getLicensePlate());
  }

  @Test
  void getCarByLicensePlate_NotFound() {
    // Arrange
    when(carRepository.findByLicensePlate("ABC123")).thenReturn(null);

    // Act
    Car result = carService.getCarByLicensePlate("ABC123");

    // Assert
    assertNull(result);
  }
}
