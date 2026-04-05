package com.parking.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parking_session")
@Getter
@Setter
public class ParkingSessionEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @ManyToOne
  @JoinColumn(name = "car_id")
  private CarEntity car;

  @ManyToOne
  @JoinColumn(name = "parking_id")
  private ParkingEntity parking;

  private LocalDateTime startTime;
  private LocalDateTime endTime;
}
