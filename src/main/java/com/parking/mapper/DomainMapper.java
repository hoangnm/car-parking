package com.parking.mapper;

import com.parking.adapter.out.persistence.entity.CarEntity;
import com.parking.adapter.out.persistence.entity.ParkingEntity;
import com.parking.adapter.out.persistence.entity.ParkingSlotEntity;
import com.parking.domain.model.Car;
import com.parking.domain.model.Parking;
import com.parking.domain.model.ParkingSlot;

public class DomainMapper {

    public static Car toDomain(CarEntity entity) {
        if (entity == null) return null;
        Car car = new Car();
        car.setId(entity.getId());
        car.setLicensePlate(entity.getLicensePlate());
        car.setBrand(entity.getBrand());
        car.setModel(entity.getModel());
        car.setColor(entity.getColor());
        return car;
    }

    public static CarEntity toEntity(Car domain) {
        if (domain == null) return null;
        CarEntity entity = new CarEntity();
        entity.setId(domain.getId());
        entity.setLicensePlate(domain.getLicensePlate());
        entity.setBrand(domain.getBrand());
        entity.setModel(domain.getModel());
        entity.setColor(domain.getColor());
        return entity;
    }

    public static Parking toDomain(ParkingEntity entity) {
        if (entity == null) return null;
        Parking parking = new Parking();
        parking.setId(entity.getId());
        parking.setLocation(entity.getLocation());
        parking.setCapacity(entity.getCapacity());
        parking.setLatitude(entity.getLatitude());
        parking.setLongitude(entity.getLongitude());
        return parking;
    }

    public static ParkingEntity toEntity(Parking domain) {
        if (domain == null) return null;
        ParkingEntity entity = new ParkingEntity();
        entity.setId(domain.getId());
        entity.setLocation(domain.getLocation());
        entity.setCapacity(domain.getCapacity());
        entity.setLatitude(domain.getLatitude());
        entity.setLongitude(domain.getLongitude());
        return entity;
    }

    public static ParkingSlot toDomain(ParkingSlotEntity entity) {
        if (entity == null) return null;
        ParkingSlot slot = new ParkingSlot();
        slot.setId(entity.getId());
        slot.setCar(toDomain(entity.getCar()));
        slot.setParking(toDomain(entity.getParking()));
        slot.setStartTime(entity.getStartTime());
        slot.setEndTime(entity.getEndTime());
        return slot;
    }

    public static ParkingSlotEntity toEntity(ParkingSlot domain) {
        if (domain == null) return null;
        ParkingSlotEntity entity = new ParkingSlotEntity();
        entity.setId(domain.getId());
        entity.setCar(toEntity(domain.getCar()));
        entity.setParking(toEntity(domain.getParking()));
        entity.setStartTime(domain.getStartTime());
        entity.setEndTime(domain.getEndTime());
        return entity;
    }
}
