package com.parking.repository;

import com.parking.adapter.out.persistence.entity.ParkingApiTokenEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ParkingApiTokenRepository extends JpaRepository<ParkingApiTokenEntity, Integer> {

  Optional<ParkingApiTokenEntity> findByToken(String token);
}
