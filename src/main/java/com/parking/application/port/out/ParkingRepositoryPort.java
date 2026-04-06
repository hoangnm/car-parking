package com.parking.application.port.out;

import com.parking.domain.model.Parking;
import java.util.Optional;

public interface ParkingRepositoryPort {
  Optional<Parking> findById(Integer id);
}
