package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.model.Car;
import com.example.demo.service.CarService;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car Controller", description = "Endpoints for managing cars in the parking lot")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Park a new car", description = "Parks a new car in the parking lot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car successfully parked"),
        @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    @PostMapping("/park")
    public ResponseEntity<Car> parkCar(@RequestBody CarDTO carDTO) {
        return ResponseEntity.ok(carService.parkCar(carDTO));
    }

    @Operation(summary = "Remove a car", description = "Removes a car from the parking lot by license plate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car successfully removed"),
        @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @PutMapping("/remove/{licensePlate}")
    public ResponseEntity<Car> removeCar(@PathVariable String licensePlate) {
        Car car = carService.removeCar(licensePlate);
        if (car != null) {
            return ResponseEntity.ok(car);
        }
        return ResponseEntity.notFound().build();
    }

    @Operation(summary = "Get all parked cars", description = "Returns a list of all cars currently parked")
    @ApiResponse(responseCode = "200", description = "Successfully retrieved list of cars")
    @GetMapping
    public ResponseEntity<List<Car>> getAllParkedCars() {
        return ResponseEntity.ok(carService.getAllParkedCars());
    }

    @Operation(summary = "Get car by license plate", description = "Returns a car by its license plate")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car found"),
        @ApiResponse(responseCode = "404", description = "Car not found")
    })
    @GetMapping("/{licensePlate}")
    public ResponseEntity<Car> getCarByLicensePlate(@PathVariable String licensePlate) {
        Car car = carService.getCarByLicensePlate(licensePlate);
        if (car != null) {
            return ResponseEntity.ok(car);
        }
        return ResponseEntity.notFound().build();
    }
}
