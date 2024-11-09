package com.example.demo.repository;

import com.example.demo.model.ParkingSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingSlotRepository extends JpaRepository<ParkingSlot, Long> {
    List<ParkingSlot> findByCarId(Long carId);
    List<ParkingSlot> findByParkingId(Long parkingId);
}
