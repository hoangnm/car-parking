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
        log.atDebug()
           .setMessage("Attempting to park car")
           .addKeyValue("action", "park_car")
           .addKeyValue("licensePlate", carDTO.getLicensePlate())
           .addKeyValue("parkingId", parkingId)
           .log();
        
        Parking parking = parkingRepository.findById(parkingId)
            .orElseThrow(() -> new ResourceNotFoundException("Parking not found with id: " + parkingId));

        // Check if parking is full
        LocalDateTime currentTime = LocalDateTime.now();
        long occupiedSlots = parkingSlotRepository.countOccupiedSlots(parkingId, currentTime);
        
        if (occupiedSlots >= parking.getCapacity()) {
            log.atWarn()
               .setMessage("Parking is full")
               .addKeyValue("action", "park_car")
               .addKeyValue("status", "full")
               .addKeyValue("parkingId", parkingId)
               .log();
            throw new ParkingException("Parking is full");
        }

        // Create or get car
        Car car = carRepository.findByLicensePlate(carDTO.getLicensePlate());
        if (car == null) {
            log.atDebug()
               .setMessage("Creating new car entry")
               .addKeyValue("action", "create_car")
               .addKeyValue("licensePlate", carDTO.getLicensePlate())
               .log();
            car = new Car();
            BeanUtils.copyProperties(carDTO, car);
            car = carRepository.save(car);
        } else {
            log.atDebug()
               .setMessage("Car found")
               .addKeyValue("id", car.getId())
               .addKeyValue("action", "check_active_slot")
               .log();
            Optional<ParkingSlot> activeParking = parkingSlotRepository
                .findByCarIdAndParkingIdAndEndTimeIsNull(car.getId(), parkingId);
            if (activeParking.isPresent()) {
                log.atWarn()
                   .setMessage("Car already parked")
                   .addKeyValue("carId", car.getId())
                   .addKeyValue("parkingId", parkingId)
                   .addKeyValue("status", "duplicate")
                   .log();
                throw new ParkingException("Car with license plate " + carDTO.getLicensePlate() + " is already parked in this parking lot");
            }
        }

        // Create parking slot
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setCar(car);
        parkingSlot.setParking(parking);
        parkingSlot.setStartTime(LocalDateTime.now());
        parkingSlot = parkingSlotRepository.save(parkingSlot);

        log.atDebug()
           .setMessage("Car parked successfully")
           .addKeyValue("action", "park_complete")
           .addKeyValue("slotId", parkingSlot.getId())
           .addKeyValue("carId", car.getId())
           .addKeyValue("parkingId", parking.getId())
           .log();

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
    public Car registerCarDeparture(Integer parkingId, String licensePlate) {
        log.atDebug()
           .setMessage("Attempting to remove car")
           .addKeyValue("action", "remove_car")
           .addKeyValue("licensePlate", licensePlate)
           .addKeyValue("parkingId", parkingId)
           .log();
        
        Car car = carRepository.findByLicensePlate(licensePlate);
        if (car == null) {
            log.atWarn()
               .setMessage("Car not found")
               .addKeyValue("action", "remove_car")
               .addKeyValue("licensePlate", licensePlate)
               .addKeyValue("status", "not_found")
               .log();
            throw new ResourceNotFoundException("Car not found with license plate: " + licensePlate);
        }
        
        Optional<ParkingSlot> activeSlot = parkingSlotRepository.findByCarIdAndParkingIdAndEndTimeIsNull(car.getId(), parkingId);
        if (!activeSlot.isPresent()) {
            log.atWarn()
               .setMessage("Car not parked in lot")
               .addKeyValue("action", "remove_car")
               .addKeyValue("carId", car.getId())
               .addKeyValue("parkingId", parkingId)
               .addKeyValue("status", "not_found")
               .log();
            throw new ParkingException("Car with license plate " + licensePlate + " is not parked in this parking lot");
        }
        
        ParkingSlot slot = activeSlot.get();
        slot.setEndTime(LocalDateTime.now());
        parkingSlotRepository.save(slot);

        log.atDebug()
           .setMessage("Car removed successfully")
           .addKeyValue("action", "remove_complete")
           .addKeyValue("carId", car.getId())
           .addKeyValue("slotId", slot.getId())
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
        return carRepository.findByLicensePlate(licensePlate);
    }
}
