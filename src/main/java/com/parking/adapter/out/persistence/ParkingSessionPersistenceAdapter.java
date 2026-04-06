package com.parking.adapter.out.persistence;

import com.parking.adapter.out.persistence.entity.ParkingSessionEntity;
import com.parking.application.port.out.ParkingSessionRepositoryPort;
import com.parking.domain.model.ParkingSession;
import com.parking.mapper.DomainMapper;
import com.parking.repository.ParkingSessionRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@SuppressWarnings("null")
public class ParkingSessionPersistenceAdapter implements ParkingSessionRepositoryPort {

  private final ParkingSessionRepository parkingSessionRepository;
  private final DomainMapper domainMapper;

  @Override
  public List<ParkingSession> findActiveSessions(Integer parkingId, LocalDateTime currentTime) {
    List<ParkingSessionEntity> entities =
        parkingSessionRepository.findActiveSessions(parkingId, currentTime);
    return entities.stream().map(domainMapper::toDomain).collect(Collectors.toList());
  }

  @Override
  public ParkingSession save(ParkingSession parkingSession) {
    ParkingSessionEntity entity = domainMapper.toEntity(parkingSession);
    entity = parkingSessionRepository.save(entity);
    return domainMapper.toDomain(entity);
  }
}
