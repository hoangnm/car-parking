package com.parking.dto;

import lombok.Data;

@Data
public class ParkingDTO {
    private String location;
    private Integer capacity;
    private Double latitude;
    private Double longitude;
}
