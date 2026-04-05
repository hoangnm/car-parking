package com.parking.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parking")
@Getter
@Setter
public class ParkingEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String location;
  private Integer capacity;
  private Double latitude;
  private Double longitude;

  @OneToMany(mappedBy = "parking")
  private List<ParkingSessionEntity> parkingSessions = new ArrayList<>();
}
