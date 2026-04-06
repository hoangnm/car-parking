package com.parking.domain.model;

import static org.junit.jupiter.api.Assertions.*;

import com.parking.exception.ParkingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ParkingTest {

  private Parking parking;
  private Car car;

  @BeforeEach
  void setUp() {
    parking = new Parking();
    parking.setId(1);
    parking.setCapacity(10);
    parking.setLocation("Test Location");

    car = new Car();
    car.setId(1);
    car.setLicensePlate("ABC-123");
  }

  @Test
  void testParkCar_Success() {
    long activeSessions = 5;
    boolean isCarAlreadyParked = false;

    ParkingSession session = parking.parkCar(car, activeSessions, isCarAlreadyParked);

    assertNotNull(session);
    assertEquals(parking, session.getParking());
    assertEquals(car, session.getCar());
    assertNotNull(session.getStartTime());
    assertNull(session.getEndTime());
  }

  @Test
  void testParkCar_ParkingFull() {
    long activeSessions = 10;
    boolean isCarAlreadyParked = false;

    ParkingException exception =
        assertThrows(
            ParkingException.class,
            () -> {
              parking.parkCar(car, activeSessions, isCarAlreadyParked);
            });

    assertEquals("Parking is full", exception.getMessage());
  }

  @Test
  void testParkCar_CarAlreadyParked() {
    long activeSessions = 5;
    boolean isCarAlreadyParked = true;

    ParkingException exception =
        assertThrows(
            ParkingException.class,
            () -> {
              parking.parkCar(car, activeSessions, isCarAlreadyParked);
            });

    assertTrue(exception.getMessage().contains("is already parked in this parking lot"));
  }
}
