package com.example.demo.controller;

import com.example.demo.dto.VehicleLocationDto;
import com.example.demo.service.VehicleLocationService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/vehicles/{id}/locations")
//@Validated
public class VehicleLocationController {
    private final VehicleLocationService vehicleLocationService;

    public VehicleLocationController(final VehicleLocationService vehicleLocationService) {
        this.vehicleLocationService = vehicleLocationService;
    }

    @PostMapping
    public VehicleLocationDto registerLocation(@Valid @RequestBody final VehicleLocationDto newVehicleLocation) {
        return vehicleLocationService.registerVehicleLocation(newVehicleLocation);
    }

}
