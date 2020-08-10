package com.example.demo.service;

import com.example.demo.dto.VehicleLocationDto;
import com.example.demo.dto.VehicleRequestDto;
import com.example.demo.dto.VehicleResponseDto;
import com.example.demo.dto.VehicleWithLocationDto;
import com.example.demo.dto.VehiclesCollectionDto;
import com.example.demo.model.Vehicle;
import com.example.demo.model.VehicleLocation;
import com.example.demo.repository.VehicleRepository;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static com.example.demo.util.Constants.SRID_4326;

@Service
public class VehicleService {
    private final VehicleRepository vehicleRepository;

    public VehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    public VehiclesCollectionDto findVehiclesWithinRectangle(double lat1, double lon1, double lat2, double lon2) {
        final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), SRID_4326);
        final Geometry geometry = geometryFactory.toGeometry(new Envelope(lat1, lat2, lon1, lon2));
        final ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
        final ZonedDateTime tenSecBefore = now.minusSeconds(10); // TODO: get rid of hardcoded value. Pass it i.e. as a request param.
        return toDto(geometry, now, tenSecBefore);
    }

    public VehicleResponseDto save(final VehicleRequestDto vehicleRequestDto) {
        final Vehicle newVehicle = new Vehicle();
        newVehicle.setDescription(vehicleRequestDto.getDescription());
        final Vehicle savedVehicle = vehicleRepository.save(newVehicle);
        final VehicleResponseDto vehicleResponseDto = new VehicleResponseDto();
        vehicleResponseDto.setId(savedVehicle.getId());
        vehicleResponseDto.setDescription(savedVehicle.getDescription());
        return vehicleResponseDto;
    }

    private VehiclesCollectionDto toDto(Geometry geometry, ZonedDateTime now, ZonedDateTime tenSecBefore) {
        final List<Vehicle> locationWithin = vehicleRepository.findVehiclesWithin(geometry, tenSecBefore, now);
        final List<VehicleWithLocationDto> vehicleWithLocationDtos = locationWithin.stream().map(vehicle -> {
            final VehicleResponseDto vehicleResponseDto = VehicleResponseDto.builder()
                    .id(vehicle.getId())
                    .description(vehicle.getDescription())
                    .build();
            final VehicleLocation location = vehicle.getLocationHistory().iterator().next();
            final VehicleLocationDto vehicleLocationDto = VehicleLocationDto.builder()
                    .latitude(location.getLocation().getX())
                    .longitude(location.getLocation().getY())
                    .registered(location.getRegistered())
                    .build();
            return VehicleWithLocationDto.builder()
                    .vehicle(vehicleResponseDto)
                    .location(vehicleLocationDto)
                    .build();
        }).collect(Collectors.toList());
        return VehiclesCollectionDto.builder().vehicles(vehicleWithLocationDtos).build();
    }

}
