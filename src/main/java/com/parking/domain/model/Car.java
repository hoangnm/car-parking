package com.parking.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Car {
  private Integer id;
  private String licensePlate;
  private String brand;
  private String model;
  private String color;

  private List<ParkingSession> parkingSessions = new ArrayList<>();
}
