package com.example.demo.service;

import com.example.demo.dto.VehicleLocationDto;
import com.example.demo.model.Vehicle;
import com.example.demo.model.VehicleLocation;
import com.example.demo.repository.VehicleLocationRepository;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import static com.example.demo.util.Constants.SRID_4326;

@Service
public class VehicleLocationService {
    private final VehicleLocationRepository vehicleLocationRepository;

    public VehicleLocationService(final VehicleLocationRepository vehicleLocationRepository) {
        this.vehicleLocationRepository = vehicleLocationRepository;
    }

    public VehicleLocationDto registerVehicleLocation(final VehicleLocationDto newVehicleLocation) {
        VehicleLocation vehicleLocation = fromDto(newVehicleLocation);
        VehicleLocation savedVehicleLocation = vehicleLocationRepository.save(vehicleLocation);
        VehicleLocationDto savedVehicleLocationDto = toDto(savedVehicleLocation);
        return savedVehicleLocationDto;
    }

    private VehicleLocation fromDto(VehicleLocationDto vehicleLocationDto) {
        VehicleLocation vehicleLocation = new VehicleLocation();
        Coordinate coordinate = new Coordinate(vehicleLocationDto.getLatitude(), vehicleLocationDto.getLongitude());
        GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID_4326);
        vehicleLocation.setLocation(geometryFactory.createPoint(coordinate));
        vehicleLocation.setRegistered(vehicleLocationDto.getRegistered());
        Vehicle vehicle = new Vehicle();
        vehicle.setId(vehicleLocationDto.getVehicleId());
        vehicleLocation.setVehicle(vehicle);
        return vehicleLocation;
    }

    private VehicleLocationDto toDto(VehicleLocation vehicleLocation) {
        VehicleLocationDto vehicleLocationDto = new VehicleLocationDto();
        vehicleLocationDto.setLatitude(vehicleLocation.getLocation().getX());
        vehicleLocationDto.setLongitude(vehicleLocation.getLocation().getY());
        vehicleLocationDto.setRegistered(vehicleLocation.getRegistered());
        vehicleLocationDto.setVehicleId(vehicleLocation.getVehicle().getId());
        return vehicleLocationDto;
    }
}
