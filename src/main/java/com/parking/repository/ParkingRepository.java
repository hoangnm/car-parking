package com.parking.repository;

import com.parking.adapter.out.persistence.entity.ParkingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParkingRepository extends JpaRepository<ParkingEntity, Integer> {
}
