package com.example.demo.controller;

import com.example.demo.dto.CarDepartureDTO;
import com.example.demo.model.Car;
import com.example.demo.service.CarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parkings")
@Tag(name = "Parking Controller", description = "Endpoints for managing parking operations")
public class ParkingController {
    
    private final CarService carService;

    public ParkingController(CarService carService) {
        this.carService = carService;
    }

    @Operation(summary = "Remove car from parking", description = "Removes a car from a specific parking slot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car successfully removed from parking"),
        @ApiResponse(responseCode = "404", description = "Car or parking not found")
    })
    @PutMapping("/{parkingId}/cars/{licensePlate}")
    public ResponseEntity<Car> removeCarFromParking(
            @PathVariable Integer parkingId,
            @PathVariable String licensePlate,
            @RequestBody CarDepartureDTO departureDTO) {
        Car removedCar = carService.removeCarFromParking(parkingId, licensePlate, departureDTO.getDepartureTime());
        if (removedCar != null) {
            return ResponseEntity.ok(removedCar);
        }
        return ResponseEntity.notFound().build();
    }
}
