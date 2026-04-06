package com.parking.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.parking.adapter.out.persistence.entity.ParkingEntity;
import com.parking.dto.CarDTO;
import com.parking.repository.CarRepository;
import com.parking.repository.ParkingRepository;
import com.parking.repository.ParkingSessionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ParkingControllerTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private ParkingRepository parkingRepository;

  @Autowired private CarRepository carRepository;

  @Autowired private ParkingSessionRepository parkingSessionRepository;

  private ParkingEntity testParking;

  @BeforeEach
  void setUp() {
    parkingSessionRepository.deleteAll();
    carRepository.deleteAll();
    parkingRepository.deleteAll();

    ParkingEntity parking = new ParkingEntity();
    parking.setLocation("Downtown Garage");
    parking.setCapacity(2);
    parking.setLatitude(10.0);
    parking.setLongitude(20.0);
    testParking = parkingRepository.save(parking);
  }

  @Test
  void testParkCar_Success() throws Exception {
    CarDTO carDTO = new CarDTO();
    carDTO.setLicensePlate("29A-12345");
    carDTO.setBrand("Toyota");
    carDTO.setModel("Camry");
    carDTO.setColor("Black");

    mockMvc
        .perform(
            post("/api/parkings/" + testParking.getId() + "/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(carDTO)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.carLicensePlate").value("29A-12345"))
        .andExpect(jsonPath("$.parkingLocation").value("Downtown Garage"));
  }

  @Test
  void testParkCar_CapacityFull() throws Exception {
    // Fill the parking lot (capacity is 2)
    CarDTO car1 = new CarDTO();
    car1.setLicensePlate("CAR-1");
    mockMvc.perform(
        post("/api/parkings/" + testParking.getId() + "/cars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(car1)));

    CarDTO car2 = new CarDTO();
    car2.setLicensePlate("CAR-2");
    mockMvc.perform(
        post("/api/parkings/" + testParking.getId() + "/cars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(car2)));

    // Attempt to park a 3rd car
    CarDTO car3 = new CarDTO();
    car3.setLicensePlate("CAR-3");

    mockMvc
        .perform(
            post("/api/parkings/" + testParking.getId() + "/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car3)))
        .andExpect(
            status()
                .isBadRequest()); // Based on @RestControllerAdvice handling exceptions, usually bad
    // request or custom. ParkingException might be mapped to 400.
    // Let's see if there's a global exception handler.
    // If not mapped, it might throw 500, we'll find out shortly when we run. If there's an
    // exception handler it is typically 400.
  }

  @Test
  void testParkCar_AlreadyParked() throws Exception {
    CarDTO car1 = new CarDTO();
    car1.setLicensePlate("CAR-1");
    mockMvc.perform(
        post("/api/parkings/" + testParking.getId() + "/cars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(car1)));

    // Attempt to park again
    mockMvc
        .perform(
            post("/api/parkings/" + testParking.getId() + "/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(car1)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void testRegisterCarDeparture_Success() throws Exception {
    CarDTO car1 = new CarDTO();
    car1.setLicensePlate("CAR-1");
    mockMvc.perform(
        post("/api/parkings/" + testParking.getId() + "/cars")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(car1)));

    mockMvc
        .perform(put("/api/parkings/" + testParking.getId() + "/cars/CAR-1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.licensePlate").value("CAR-1"));
  }
}
