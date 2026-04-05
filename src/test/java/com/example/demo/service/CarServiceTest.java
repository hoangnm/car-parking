package com.example.demo.service;

import com.parking.dto.CarDTO;
import com.parking.dto.ParkingSlotDTO;
import com.parking.domain.model.Car;
import com.parking.adapter.out.persistence.entity.CarEntity;
import com.parking.adapter.out.persistence.entity.ParkingEntity;
import com.parking.adapter.out.persistence.entity.ParkingSlotEntity;
import com.parking.repository.CarRepository;
import com.parking.repository.ParkingRepository;
import com.parking.repository.ParkingSlotRepository;
import com.parking.service.CarService;
import com.parking.exception.ParkingException;
import com.parking.exception.ResourceNotFoundException;

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

    private CarEntity testCarEntity;
    private ParkingEntity testParkingEntity;
    private ParkingSlotEntity testParkingSlotEntity;
    private CarDTO testCarDTO;

    @BeforeEach
    void setUp() {
        carService = new CarService(carRepository, parkingRepository, parkingSlotRepository);

        // Setup test data
        testCarEntity = new CarEntity();
        testCarEntity.setId(1);
        testCarEntity.setLicensePlate("ABC123");

        testParkingEntity = new ParkingEntity();
        testParkingEntity.setId(1);
        testParkingEntity.setCapacity(100);
        testParkingEntity.setLocation("Test Location");

        testParkingSlotEntity = new ParkingSlotEntity();
        testParkingSlotEntity.setId(1);
        testParkingSlotEntity.setCar(testCarEntity);
        testParkingSlotEntity.setParking(testParkingEntity);
        testParkingSlotEntity.setStartTime(LocalDateTime.now());

        testCarDTO = new CarDTO();
        testCarDTO.setLicensePlate("ABC123");
    }

    @Test
    void parkCar_Success() {
        // Arrange
        when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
        when(parkingSlotRepository.countOccupiedSlots(anyInt(), any())).thenReturn(0L);
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(null);
        when(carRepository.save(any(CarEntity.class))).thenReturn(testCarEntity);
        when(parkingSlotRepository.save(any(ParkingSlotEntity.class))).thenReturn(testParkingSlotEntity);

        // Act
        ParkingSlotDTO result = carService.parkCar(testCarDTO, 1);

        // Assert
        assertNotNull(result);
        assertEquals(testParkingSlotEntity.getId(), result.getId());
        assertEquals(testCarEntity.getId(), result.getCarId());
        assertEquals(testParkingEntity.getId(), result.getParkingId());
        verify(parkingSlotRepository).save(any(ParkingSlotEntity.class));
    }

    @Test
    void parkCar_ParkingFull() {
        // Arrange
        when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
        when(parkingSlotRepository.countOccupiedSlots(anyInt(), any())).thenReturn(100L);

        // Act & Assert
        assertThrows(ParkingException.class, () -> carService.parkCar(testCarDTO, 1));
    }

    @Test
    void parkCar_CarAlreadyParked() {
        // Arrange
        when(parkingRepository.findById(1)).thenReturn(Optional.of(testParkingEntity));
        when(parkingSlotRepository.countOccupiedSlots(anyInt(), any())).thenReturn(0L);
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCarEntity);
        when(parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.of(testParkingSlotEntity));

        // Act & Assert
        assertThrows(ParkingException.class, () -> carService.parkCar(testCarDTO, 1));
    }

    @Test
    void removeCarFromParking_Success() {
        // Arrange
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCarEntity);
        when(parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.of(testParkingSlotEntity));
        when(parkingSlotRepository.save(any(ParkingSlotEntity.class))).thenReturn(testParkingSlotEntity);

        // Act
        Car result = carService.registerCarDeparture(1, "ABC123");

        // Assert
        assertNotNull(result);
        assertEquals(testCarEntity.getId(), result.getId());
        assertEquals(testCarEntity.getLicensePlate(), result.getLicensePlate());
        verify(parkingSlotRepository).save(any(ParkingSlotEntity.class));
    }

    @Test
    void removeCarFromParking_CarNotFound() {
        // Arrange
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(null);

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, 
            () -> carService.registerCarDeparture(1, "ABC123"));
    }

    @Test
    void removeCarFromParking_CarNotParked() {
        // Arrange
        when(carRepository.findByLicensePlate("ABC123")).thenReturn(testCarEntity);
        when(parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(anyInt(), anyInt()))
            .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ParkingException.class, 
            () -> carService.registerCarDeparture(1, "ABC123"));
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
