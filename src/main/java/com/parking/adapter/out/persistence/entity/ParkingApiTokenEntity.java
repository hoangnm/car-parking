package com.parking.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "parking_api_token")
@Getter
@Setter
public class ParkingApiTokenEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Integer id;

  @Column(name = "parking_id", nullable = false, unique = true)
  private Integer parkingId;

  @Column(nullable = false, unique = true)
  private String token;
}
