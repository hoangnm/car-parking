package com.parking.application.port.out;

import com.parking.domain.model.ParkingSession;
import java.time.LocalDateTime;
import java.util.List;

public interface ParkingSessionRepositoryPort {
  List<ParkingSession> findActiveSessions(Integer parkingId, LocalDateTime currentTime);

  ParkingSession save(ParkingSession parkingSession);
}
