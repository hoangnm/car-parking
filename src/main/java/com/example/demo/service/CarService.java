package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.ParkingSlotDTO;
import com.example.demo.model.Car;
import com.example.demo.model.Parking;
import com.example.demo.model.ParkingSlot;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.ParkingRepository;
import com.example.demo.repository.ParkingSlotRepository;
import com.example.demo.exception.*;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class CarService {
    private final CarRepository carRepository;
    private final ParkingRepository parkingRepository;
    private final ParkingSlotRepository parkingSlotRepository;

    public CarService(CarRepository carRepository, 
                     ParkingRepository parkingRepository,
                     ParkingSlotRepository parkingSlotRepository) {
        this.carRepository = carRepository;
        this.parkingRepository = parkingRepository;
        this.parkingSlotRepository = parkingSlotRepository;
    }

    @Transactional
    public ParkingSlotDTO parkCar(CarDTO carDTO, Integer parkingId) {
        log.debug("Attempting to park car with license plate: {} in parking ID: {}", carDTO.getLicensePlate(), parkingId);
        
        Parking parking = parkingRepository.findById(parkingId)
            .orElseThrow(() -> new ResourceNotFoundException("Parking not found with id: " + parkingId));

        // Check if parking is full
        LocalDateTime currentTime = LocalDateTime.now();
        long occupiedSlots = parkingSlotRepository.countOccupiedSlots(parkingId, currentTime);
        
        if (occupiedSlots >= parking.getCapacity()) {
            log.warn("Parking is full for parking ID: {}", parkingId);
            throw new ParkingException("Parking is full");
        }

        // Create or get car
        Car car = carRepository.findByLicensePlate(carDTO.getLicensePlate());
        if (car == null) {
            log.debug("Car not found, creating new car entry for license plate: {}", carDTO.getLicensePlate());
            car = new Car();
            BeanUtils.copyProperties(carDTO, car);
            car = carRepository.save(car);
        } else {
            log.debug("Car found with ID: {}, checking for active parking slot", car.getId());
            Optional<ParkingSlot> activeParking = parkingSlotRepository
                .findByCarIdAndParkingIdAndEndTimeIsNull(car.getId(), parkingId);
            if (activeParking.isPresent()) {
                log.warn("Car with ID: {} is already parked in parking ID: {}", car.getId(), parkingId);
                throw new ParkingException("Car with license plate " + carDTO.getLicensePlate() + " is already parked in this parking lot");
            }
        }

        // Create parking slot
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setCar(car);
        parkingSlot.setParking(parking);
        parkingSlot.setStartTime(LocalDateTime.now());
        parkingSlot = parkingSlotRepository.save(parkingSlot);

        log.debug("Car parked successfully with parking slot ID: {}", parkingSlot.getId());

        // Convert to DTO
        ParkingSlotDTO parkingSlotDTO = new ParkingSlotDTO();
        parkingSlotDTO.setId(parkingSlot.getId());
        parkingSlotDTO.setCarId(car.getId());
        parkingSlotDTO.setParkingId(parking.getId());
        parkingSlotDTO.setStartTime(parkingSlot.getStartTime());
        parkingSlotDTO.setCarLicensePlate(car.getLicensePlate());
        parkingSlotDTO.setParkingLocation(parking.getLocation());

        return parkingSlotDTO;
    }

    @Transactional
    public Car removeCarFromParking(Integer parkingId, String licensePlate, LocalDateTime departureTime) {
        log.debug("Attempting to remove car with license plate: {} from parking ID: {}", licensePlate, parkingId);
        
        Car car = carRepository.findByLicensePlate(licensePlate);
        if (car == null) {
            log.warn("Car not found with license plate: {}", licensePlate);
            throw new ResourceNotFoundException("Car not found with license plate: " + licensePlate);
        }
        
        Optional<ParkingSlot> activeSlot = parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(car.getId(), parkingId);
        if (!activeSlot.isPresent()) {
            log.warn("Car with ID: {} is not parked in parking ID: {}", car.getId(), parkingId);
            throw new ParkingException("Car with license plate " + licensePlate + " is not parked in this parking lot");
        }
        
        ParkingSlot slot = activeSlot.get();
        slot.setEndTime(departureTime != null ? departureTime : LocalDateTime.now());
        parkingSlotRepository.save(slot);

        log.debug("Car with ID: {} removed from parking slot ID: {}", car.getId(), slot.getId());

        return car;
    }

    public Car getCarByLicensePlate(String licensePlate) {
        log.debug("Fetching car with license plate: {}", licensePlate);
        return carRepository.findByLicensePlate(licensePlate);
    }
}
