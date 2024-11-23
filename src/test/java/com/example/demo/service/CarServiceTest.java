package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.ParkingSlotDTO;
import com.example.demo.model.Car;
import com.example.demo.model.Parking;
import com.example.demo.model.ParkingSlot;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.ParkingRepository;
import com.example.demo.repository.ParkingSlotRepository;
import com.example.demo.exception.ParkingException;
import com.example.demo.exception.ResourceNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {

    @Mock
    private CarRepository carRepository;

    @Mock
    private ParkingRepository parkingRepository;

    @Mock
    private ParkingSlotRepository parkingSlotRepository;

    private CarService carService;

    private Car testCar;
    private Parking testParking;
    private ParkingSlot testParkingSlot;
    private CarDTO testCarDTO;

    @BeforeEach
    void setUp() {
        carService = new CarService(carRepository, parkingRepository, parkingSlotRepository);

        // Setup test data
        testCar = new Car();
        testCar.setId(1);
        testCar.setLicensePlate("ABC123");

        testParking = new Parking();
        testParking.setId(1);
        testParking.setCapacity(100);
        testParking.setLocation("Test Location");

        testParkingSlot = new ParkingSlot();
        testParkingSlot.setId(1);
        testParkingSlot.setCar(testCar);
        testParkingSlot.setParking(testParking);
        testParkingSlot.setStartTime(LocalDateTime.now());

        testCarDTO = new CarDTO();
        testCarDTO.setLicensePlate("ABC123");
    }

    @Test
    void parkCar_Success() {
        // Arrange
        when(parkingRepository.findById(1)).thenReturn(Optional.of(testParking));
        when(parkingSlotRepository.countOccupiedSlots(anyInt(), any())).thenReturn(0L);
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(null);
        when(carRepository.save(any(Car.class))).thenReturn(testCar);
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenReturn(testParkingSlot);

        // Act
        ParkingSlotDTO result = carService.parkCar(testCarDTO, 1);

        // Assert
        assertNotNull(result);
        assertEquals(testParkingSlot.getId(), result.getId());
        assertEquals(testCar.getId(), result.getCarId());
        assertEquals(testParking.getId(), result.getParkingId());
        verify(parkingSlotRepository).save(any(ParkingSlot.class));
    }

    @Test
    void parkCar_ParkingFull() {
        // Arrange
        when(parkingRepository.findById(1)).thenReturn(Optional.of(testParking));
        when(parkingSlotRepository.countOccupiedSlots(anyInt(), any())).thenReturn(100L);

        // Act & Assert
        assertThrows(ParkingException.class, () -> carService.parkCar(testCarDTO, 1));
    }

    @Test
    void parkCar_CarAlreadyParked() {
        // Arrange
        when(parkingRepository.findById(1)).thenReturn(Optional.of(testParking));
        when(parkingSlotRepository.countOccupiedSlots(anyInt(), any())).thenReturn(0L);
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCar);
        when(parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.of(testParkingSlot));

        // Act & Assert
        assertThrows(ParkingException.class, () -> carService.parkCar(testCarDTO, 1));
    }

    @Test
    void removeCarFromParking_Success() {
        // Arrange
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCar);
        when(parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.of(testParkingSlot));
        when(parkingSlotRepository.save(any(ParkingSlot.class))).thenReturn(testParkingSlot);

        // Act
        Car result = carService.removeCarFromParking(1, "ABC123", LocalDateTime.now());

        // Assert
        assertNotNull(result);
        assertEquals(testCar.getId(), result.getId());
        assertEquals(testCar.getLicensePlate(), result.getLicensePlate());
        verify(parkingSlotRepository).save(any(ParkingSlot.class));
    }

    @Test
    void removeCarFromParking_CarNotFound() {
        // Arrange
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> carService.removeCarFromParking(1, "ABC123", LocalDateTime.now()));
    }

    @Test
    void removeCarFromParking_CarNotParked() {
        // Arrange
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCar);
        when(parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ParkingException.class, 
            () -> carService.removeCarFromParking(1, "ABC123", LocalDateTime.now()));
    }

    @Test
    void getCarByLicensePlate_Success() {
        // Arrange
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCar);

        // Act
        Car result = carService.getCarByLicensePlate("ABC123");

        // Assert
        assertNotNull(result);
        assertEquals(testCar.getId(), result.getId());
        assertEquals(testCar.getLicensePlate(), result.getLicensePlate());
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
