package com.parking.application.port.in;

import com.parking.domain.model.Car;

public interface DepartCarUseCase {
  Car registerCarDeparture(Integer parkingId, String licensePlate);
}
