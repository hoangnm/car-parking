package com.parking.adapter.out.persistence;

import com.parking.adapter.out.persistence.entity.CarEntity;
import com.parking.application.port.out.CarRepositoryPort;
import com.parking.domain.model.Car;
import com.parking.mapper.DomainMapper;
import com.parking.repository.CarRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class CarPersistenceAdapter implements CarRepositoryPort {

  private final CarRepository carRepository;
  private final DomainMapper domainMapper;

  @Override
  public Car findByLicensePlate(String licensePlate) {
    CarEntity entity = carRepository.findByLicensePlate(licensePlate);
    return domainMapper.toDomain(entity);
  }

  @Override
  public Car save(Car car) {
    CarEntity entity = domainMapper.toEntity(car);
    entity = carRepository.save(entity);
    return domainMapper.toDomain(entity);
  }
}
