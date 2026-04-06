package com.parking.application.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.parking.application.port.out.CarRepositoryPort;
import com.parking.application.port.out.ParkingRepositoryPort;
import com.parking.application.port.out.ParkingSessionRepositoryPort;
import com.parking.domain.exception.CarNotFoundException;
import com.parking.domain.exception.ParkingException;
import com.parking.domain.exception.ParkingNotFoundException;
import com.parking.domain.model.Car;
import com.parking.domain.model.Parking;
import com.parking.domain.model.ParkingSession;
import com.parking.dto.CarDTO;
import com.parking.dto.ParkingSessionDTO;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ParkingServiceTest {

  @Mock private CarRepositoryPort carRepositoryPort;

  @Mock private ParkingRepositoryPort parkingRepositoryPort;

  @Mock private ParkingSessionRepositoryPort parkingSessionRepositoryPort;

  private ParkingService parkingService;

  private Car testCar;
  private Parking testParking;
  private ParkingSession testParkingSession;
  private CarDTO testCarDTO;

  @BeforeEach
  void setUp() {
    parkingService =
        new ParkingService(carRepositoryPort, parkingRepositoryPort, parkingSessionRepositoryPort);

    // Setup test data
    testCar = new Car();
    testCar.setId(1);
    testCar.setLicensePlate("ABC123");

    testParking = new Parking();
    testParking.setId(1);
    testParking.setCapacity(100);
    testParking.setLocation("Test Location");

    testParkingSession = new ParkingSession();
    testParkingSession.setId(1);
    testParkingSession.setCar(testCar);
    testParkingSession.setParking(testParking);
    testParkingSession.setStartTime(LocalDateTime.now());

    testCarDTO = new CarDTO();
    testCarDTO.setLicensePlate("ABC123");
  }

  @Test
  void parkCar_Success() {
    // Arrange
    when(parkingRepositoryPort.findById(1)).thenReturn(Optional.of(testParking));
    when(parkingSessionRepositoryPort.findActiveSessions(anyInt(), any()))
        .thenReturn(new java.util.ArrayList<>());
    when(carRepositoryPort.findByLicensePlate("ABC123")).thenReturn(null);
    when(carRepositoryPort.save(any(Car.class))).thenReturn(testCar);
    when(parkingSessionRepositoryPort.save(any(ParkingSession.class)))
        .thenReturn(testParkingSession);

    // Act
    ParkingSessionDTO result = parkingService.parkCar(testCarDTO, 1);

    // Assert
    assertNotNull(result);
    assertEquals(testParkingSession.getId(), result.getId());
    assertEquals(testCar.getId(), result.getCarId());
    assertEquals(testParking.getId(), result.getParkingId());
    verify(parkingSessionRepositoryPort).save(any(ParkingSession.class));
  }

  @Test
  void parkCar_ParkingFull() {
    // Arrange
    when(parkingRepositoryPort.findById(1)).thenReturn(Optional.of(testParking));
    java.util.List<ParkingSession> fullSessions = new java.util.ArrayList<>();
    for (int i = 0; i < 100; i++) fullSessions.add(new ParkingSession());
    when(parkingSessionRepositoryPort.findActiveSessions(anyInt(), any())).thenReturn(fullSessions);

    // Act & Assert
    assertThrows(ParkingException.class, () -> parkingService.parkCar(testCarDTO, 1));
  }

  @Test
  void parkCar_ParkingNotFound() {
    // Arrange
    when(parkingRepositoryPort.findById(1)).thenReturn(Optional.empty());

    // Act & Assert
    assertThrows(ParkingNotFoundException.class, () -> parkingService.parkCar(testCarDTO, 1));
  }

  @Test
  void parkCar_CarAlreadyParked() {
    // Arrange
    when(parkingRepositoryPort.findById(1)).thenReturn(Optional.of(testParking));
    when(parkingSessionRepositoryPort.findActiveSessions(anyInt(), any()))
        .thenReturn(java.util.List.of(testParkingSession));
    when(carRepositoryPort.findByLicensePlate("ABC123")).thenReturn(testCar);

    // Act & Assert
    assertThrows(ParkingException.class, () -> parkingService.parkCar(testCarDTO, 1));
  }

  @Test
  void removeCarFromParking_Success() {
    // Arrange
    when(carRepositoryPort.findByLicensePlate("ABC123")).thenReturn(testCar);
    when(parkingRepositoryPort.findById(1)).thenReturn(Optional.of(testParking));
    when(parkingSessionRepositoryPort.findActiveSessions(anyInt(), any()))
        .thenReturn(java.util.List.of(testParkingSession));
    when(parkingSessionRepositoryPort.save(any(ParkingSession.class)))
        .thenReturn(testParkingSession);

    // Act
    Car result = parkingService.registerCarDeparture(1, "ABC123");

    // Assert
    assertNotNull(result);
    assertEquals(testCar.getId(), result.getId());
    assertEquals(testCar.getLicensePlate(), result.getLicensePlate());
    verify(parkingSessionRepositoryPort).save(any(ParkingSession.class));
  }

  @Test
  void removeCarFromParking_CarNotFound() {
    // Arrange
    when(carRepositoryPort.findByLicensePlate("ABC123")).thenReturn(null);

    // Act & Assert
    assertThrows(
        CarNotFoundException.class, () -> parkingService.registerCarDeparture(1, "ABC123"));
  }

  @Test
  void removeCarFromParking_CarNotParked() {
    // Arrange
    when(carRepositoryPort.findByLicensePlate("ABC123")).thenReturn(testCar);
    when(parkingRepositoryPort.findById(1)).thenReturn(Optional.of(testParking));
    when(parkingSessionRepositoryPort.findActiveSessions(anyInt(), any()))
        .thenReturn(new java.util.ArrayList<>());

    // Act & Assert
    assertThrows(ParkingException.class, () -> parkingService.registerCarDeparture(1, "ABC123"));
  }
}
