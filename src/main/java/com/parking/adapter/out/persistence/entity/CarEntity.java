package com.parking.adapter.out.persistence.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "car")
@Getter
@Setter
public class CarEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  private String licensePlate;
  private String brand;
  private String model;
  private String color;

  @OneToMany(mappedBy = "car")
  private List<ParkingSessionEntity> parkingSessions = new ArrayList<>();
}
