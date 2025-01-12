package com.example.demo.controller;

import com.example.demo.dto.CarDTO;
import com.example.demo.dto.ParkingSlotDTO;
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

    @Operation(summary = "Park a car", description = "Parks a car in a specific parking lot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car successfully parked"),
        @ApiResponse(responseCode = "400", description = "Invalid input or parking is full"),
        @ApiResponse(responseCode = "404", description = "Parking lot not found")
    })
    @PostMapping("/{parkingId}/cars")
    public ResponseEntity<ParkingSlotDTO> parkCar(@RequestBody CarDTO carDTO, @PathVariable Integer parkingId) {
        ParkingSlotDTO parkingSlot = carService.parkCar(carDTO, parkingId);
        return ResponseEntity.ok(parkingSlot);
    }

    @Operation(summary = "Remove car from parking", description = "Removes a car from a specific parking slot")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Car successfully removed from parking"),
        @ApiResponse(responseCode = "404", description = "Car or parking not found")
    })
    @PutMapping("/{parkingId}/cars/{licensePlate}")
    public ResponseEntity<Car> registerCarDeparture(
            @PathVariable Integer parkingId,
            @PathVariable String licensePlate) {
        Car removedCar = carService.registerCarDeparture(parkingId, licensePlate);
        if (removedCar != null) {
            return ResponseEntity.ok(removedCar);
        }
        return ResponseEntity.notFound().build();
    }
}
