package com.parking.mapper;

import com.parking.adapter.out.persistence.entity.CarEntity;
import com.parking.adapter.out.persistence.entity.ParkingEntity;
import com.parking.adapter.out.persistence.entity.ParkingSessionEntity;
import com.parking.domain.model.Car;
import com.parking.domain.model.Parking;
import com.parking.domain.model.ParkingSession;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface DomainMapper {

  Car toDomain(CarEntity entity);

  CarEntity toEntity(Car domain);

  Parking toDomain(ParkingEntity entity);

  ParkingEntity toEntity(Parking domain);

  ParkingSession toDomain(ParkingSessionEntity entity);

  ParkingSessionEntity toEntity(ParkingSession domain);
}
