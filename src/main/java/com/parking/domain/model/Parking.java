package com.parking.domain.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Parking {
  private Integer id;
  private String location;
  private Integer capacity;
  private Double latitude;
  private Double longitude;

  private List<ParkingSession> parkingSessions = new ArrayList<>();
}
