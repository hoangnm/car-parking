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

@RestController
@RequestMapping("/api/cars")
@Tag(name = "Car Controller", description = "Endpoints for managing cars in the parking lot")
public class CarController {
    private final CarService carService;

    public CarController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Park a car", description = "Parks a car in a specific parking lot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car successfully parked"),
        @ApiResponse(responseCode = "400", description = "Invalid input or parking is full"),
        @ApiResponse(responseCode = "404", description = "Parking lot not found")
    })
    @PostMapping("/park/{parkingId}")
    public ResponseEntity<Car> parkCar(@RequestBody CarDTO carDTO, @PathVariable Integer parkingId) {
        try {
            return ResponseEntity.ok(carService.parkCar(carDTO, parkingId));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
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
