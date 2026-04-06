package com.parking.adapter.out.persistence;

import com.parking.adapter.out.persistence.entity.ParkingEntity;
import com.parking.application.port.out.ParkingRepositoryPort;
import com.parking.domain.model.Parking;
import com.parking.mapper.DomainMapper;
import com.parking.repository.ParkingRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ParkingPersistenceAdapter implements ParkingRepositoryPort {

  private final ParkingRepository parkingRepository;
  private final DomainMapper domainMapper;

  @Override
  public Optional<Parking> findById(Integer id) {
    Optional<ParkingEntity> entity = parkingRepository.findById(id);
    return entity.map(domainMapper::toDomain);
  }
}
