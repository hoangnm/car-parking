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
    ParkingSession session = parking.parkCar(car);

    assertNotNull(session);
    assertEquals(parking, session.getParking());
    assertEquals(car, session.getCar());
    assertNotNull(session.getStartTime());
    assertNull(session.getEndTime());
  }

  @Test
  void testParkCar_ParkingFull() {
    for (int i = 0; i < 10; i++) {
      Car dummyCar = new Car();
      dummyCar.setLicensePlate("DUMMY-" + i);
      ParkingSession dummySession = new ParkingSession();
      dummySession.setCar(dummyCar);
      parking.getActiveSessions().add(dummySession);
    }

    ParkingException exception =
        assertThrows(
            ParkingException.class,
            () -> {
              parking.parkCar(car);
            });

    assertEquals("Parking is full", exception.getMessage());
  }

  @Test
  void testParkCar_CarAlreadyParked() {
    ParkingSession dummySession = new ParkingSession();
    dummySession.setCar(car);
    parking.getActiveSessions().add(dummySession);

    ParkingException exception =
        assertThrows(
            ParkingException.class,
            () -> {
              parking.parkCar(car);
            });

    assertTrue(exception.getMessage().contains("is already parked in this parking lot"));
  }
}
