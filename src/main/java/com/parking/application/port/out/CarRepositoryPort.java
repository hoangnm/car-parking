package com.parking.application.port.out;

import com.parking.domain.model.Car;

public interface CarRepositoryPort {
  Car findByLicensePlate(String licensePlate);

  Car save(Car car);
}
