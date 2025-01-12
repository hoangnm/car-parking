package com.parking.repository;

import com.parking.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Integer> {
    List<ParkingSlot> findByCarId(Integer carId);
    List<ParkingSlot> findByParkingId(Integer parkingId);
    
    @Query("SELECT COUNT(ps) FROM ParkingSlot ps WHERE ps.parking.id = :parkingId AND (ps.endTime IS NULL OR ps.endTime > :currentTime)")
    long countOccupiedSlots(@Param("parkingId") Integer parkingId, @Param("currentTime") LocalDateTime currentTime);
    
    Optional<ParkingSlot> findByCarIdAndParkingIdAndEndTimeIsNull(Integer carId, Integer parkingId);
}
