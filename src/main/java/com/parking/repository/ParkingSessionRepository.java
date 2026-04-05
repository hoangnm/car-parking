package com.parking.repository;

import com.parking.adapter.out.persistence.entity.ParkingSessionEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSessionRepository extends JpaRepository<ParkingSessionEntity, Integer> {
  List<ParkingSessionEntity> findByCarId(Integer carId);

  List<ParkingSessionEntity> findByParkingId(Integer parkingId);

  @Query(
      "SELECT COUNT(ps) FROM ParkingSessionEntity ps WHERE ps.parking.id = :parkingId AND (ps.endTime IS NULL OR ps.endTime > :currentTime)")
  long countActiveSessions(
      @Param("parkingId") Integer parkingId, @Param("currentTime") LocalDateTime currentTime);

  Optional<ParkingSessionEntity> findByCarIdAndParkingIdAndEndTimeIsNull(
      Integer carId, Integer parkingId);
}
