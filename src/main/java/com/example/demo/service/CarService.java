package com.example.demo.service;

import com.example.demo.dto.CarDTO;
import com.example.demo.model.Car;
import com.example.demo.repository.CarRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {
    private final CarRepository carRepository;

    public CarService(CarRepository carRepository) {
        this.carRepository = carRepository;
    }

    public Car parkCar(CarDTO carDTO) {
        Car car = new Car();
        BeanUtils.copyProperties(carDTO, car);
        car.setParked(true);
        return carRepository.save(car);
    }

    public Car removeCar(String licensePlate) {
        Car car = carRepository.findByLicensePlate(licensePlate);
        if (car != null) {
            car.setParked(false);
            return carRepository.save(car);
        }
        return null;
    }

    public List<Car> getAllParkedCars() {
        return carRepository.findAll();
    }

    public Car getCarByLicensePlate(String licensePlate) {
        return carRepository.findByLicensePlate(licensePlate);
    }
}
