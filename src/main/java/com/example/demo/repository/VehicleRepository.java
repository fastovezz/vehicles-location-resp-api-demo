package com.example.demo.repository;

import com.example.demo.model.Vehicle;
import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.time.ZonedDateTime;
import java.util.List;

public interface VehicleRepository extends CrudRepository<Vehicle, Long> {
    @Query("SELECT v FROM Vehicle v JOIN FETCH v.locationHistory l " +
            "WHERE within(l.location, :geometry) = TRUE " +
            "AND l.registered BETWEEN :start AND :end")
    List<Vehicle> findVehiclesWithin(@Param("geometry") Geometry geometry,
                                     @Param("start") ZonedDateTime start,
                                     @Param("end") ZonedDateTime end);

}
