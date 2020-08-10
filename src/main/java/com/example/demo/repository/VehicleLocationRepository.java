package com.example.demo.repository;

import com.example.demo.model.VehicleLocation;
import org.springframework.data.repository.CrudRepository;

public interface VehicleLocationRepository extends CrudRepository<VehicleLocation, Long> {
}
