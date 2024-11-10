package com.example.demo.repository;

import com.example.demo.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Integer> {
    List<ParkingSlot> findByCarId(Integer carId);
    List<ParkingSlot> findByParkingId(Integer parkingId);
}
