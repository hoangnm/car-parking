package com.parking.application.port.in;

import com.parking.dto.CarDTO;
import com.parking.dto.ParkingSessionDTO;

public interface ParkCarUseCase {
  ParkingSessionDTO parkCar(CarDTO carDTO, Integer parkingId);
}
