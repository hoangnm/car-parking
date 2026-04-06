package com.parking.adapter.in.web;

import com.parking.application.port.in.DepartCarUseCase;
import com.parking.application.port.in.ParkCarUseCase;
import com.parking.domain.model.Car;
import com.parking.dto.CarDTO;
import com.parking.dto.ParkingSessionDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/parkings")
@Tag(name = "Parking Controller", description = "Endpoints for managing parking operations")
public class ParkingController {

  private final ParkCarUseCase parkCarUseCase;
  private final DepartCarUseCase departCarUseCase;

  public ParkingController(ParkCarUseCase parkCarUseCase, DepartCarUseCase departCarUseCase) {
    this.parkCarUseCase = parkCarUseCase;
    this.departCarUseCase = departCarUseCase;
  }

  @Operation(summary = "Park a car", description = "Parks a car in a specific parking lot")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Car successfully parked"),
        @ApiResponse(responseCode = "400", description = "Invalid input or parking is full"),
        @ApiResponse(responseCode = "404", description = "Parking lot not found")
      })
  @PostMapping("/{parkingId}/cars")
  public ResponseEntity<ParkingSessionDTO> parkCar(
      @RequestBody CarDTO carDTO, @PathVariable @NonNull Integer parkingId) {
    ParkingSessionDTO parkingSession = parkCarUseCase.parkCar(carDTO, parkingId);
    return ResponseEntity.ok(parkingSession);
  }

  @Operation(
      summary = "Remove car from parking",
      description = "Removes a car from a specific parking slot")
  @ApiResponses(
      value = {
        @ApiResponse(responseCode = "200", description = "Car successfully removed from parking"),
        @ApiResponse(responseCode = "404", description = "Car or parking not found")
      })
  @PutMapping("/{parkingId}/cars/{licensePlate}")
  public ResponseEntity<Car> registerCarDeparture(
      @PathVariable @NonNull Integer parkingId, @PathVariable String licensePlate) {
    Car removedCar = departCarUseCase.registerCarDeparture(parkingId, licensePlate);
    if (removedCar != null) {
      return ResponseEntity.ok(removedCar);
    }
    return ResponseEntity.notFound().build();
  }
}
