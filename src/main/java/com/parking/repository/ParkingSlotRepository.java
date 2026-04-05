package com.parking.repository;

import com.parking.adapter.out.persistence.entity.ParkingSlotEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlotEntity, Integer> {
    List<ParkingSlotEntity> findByCarId(Integer carId);
    List<ParkingSlotEntity> findByParkingId(Integer parkingId);
    
    @Query("SELECT COUNT(ps) FROM ParkingSlotEntity ps WHERE ps.parking.id = :parkingId AND (ps.endTime IS NULL OR ps.endTime > :currentTime)")
    long countOccupiedSlots(@Param("parkingId") Integer parkingId, @Param("currentTime") LocalDateTime currentTime);
    
    Optional<ParkingSlotEntity> findByCarIdAndParkingIdAndEndTimeIsNull(Integer carId, Integer parkingId);
}
