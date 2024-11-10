package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.model.Car;
import com.example.demo.model.Parking;
import com.example.demo.model.ParkingSlot;
import com.example.demo.repository.CarRepository;
import com.example.demo.repository.ParkingRepository;
import com.example.demo.repository.ParkingSlotRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

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
    public Car parkCar(CarDTO carDTO, Integer parkingId) {
        Parking parking = parkingRepository.findById(parkingId)
            .orElseThrow(() -> new RuntimeException("Parking not found"));

        // Check if parking is full
        long occupiedSlots = parkingSlotRepository.findByParkingId(parkingId).stream()
            .filter(slot -> slot.getEndTime() == null)
            .count();
        
        if (occupiedSlots >= parking.getCapacity()) {
            throw new RuntimeException("Parking is full");
        }

        // Create or get car
        Car car = carRepository.findByLicensePlate(carDTO.getLicensePlate());
        if (car == null) {
            car = new Car();
            BeanUtils.copyProperties(carDTO, car);
            car = carRepository.save(car);
        }

        // Create parking slot
        ParkingSlot parkingSlot = new ParkingSlot();
        parkingSlot.setCar(car);
        parkingSlot.setParking(parking);
        parkingSlot.setStartTime(LocalDateTime.now());
        parkingSlotRepository.save(parkingSlot);

        return car;
    }

    @Transactional
    public Car removeCar(String licensePlate) {
        Car car = carRepository.findByLicensePlate(licensePlate);
        if (car != null) {
            List<ParkingSlot> activeSlots = parkingSlotRepository.findByCarId(car.getId()).stream()
                .filter(slot -> slot.getEndTime() == null)
                .collect(Collectors.toList());
            
            activeSlots.forEach(slot -> {
                slot.setEndTime(LocalDateTime.now());
                parkingSlotRepository.save(slot);
            });
            
            return car;
        }
        return null;
    }

    public List<Car> getAllParkedCars() {
        return parkingSlotRepository.findAll().stream()
            .filter(slot -> slot.getEndTime() == null)
            .map(ParkingSlot::getCar)
            .distinct()
            .collect(Collectors.toList());
    }

    public Car getCarByLicensePlate(String licensePlate) {
        return carRepository.findByLicensePlate(licensePlate);
    }
}
