package com.example.demo.controller;

import com.example.demo.dto.VehicleRequestDto;
import com.example.demo.dto.VehicleResponseDto;
import com.example.demo.dto.VehiclesCollectionDto;
import com.example.demo.service.VehicleService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;

import static com.example.demo.util.Constants.LATITUDE_ERR_MSG;
import static com.example.demo.util.Constants.LATITUDE_MAX_VAL;
import static com.example.demo.util.Constants.LATITUDE_MIN_VAL;
import static com.example.demo.util.Constants.LONGITUDE_ERR_MSG;
import static com.example.demo.util.Constants.LONGITUDE_MAX_VAL;
import static com.example.demo.util.Constants.LONGITUDE_MIN_VAL;

@RestController
@RequestMapping("/vehicles")
@Validated
public class VehicleController {
    private final VehicleService vehicleService;

    public VehicleController(final VehicleService vehicleService) {
        this.vehicleService = vehicleService;
    }

    @GetMapping(params = {"lat1", "lon1", "lat2", "lon2"})
    public VehiclesCollectionDto getVehiclesInRectangle(@RequestParam
                                                        @DecimalMin(value = LATITUDE_MIN_VAL, message = LATITUDE_ERR_MSG)
                                                        @DecimalMax(value = LATITUDE_MAX_VAL, message = LATITUDE_ERR_MSG)
                                                                double lat1,
                                                        @RequestParam
                                                        @DecimalMin(value = LONGITUDE_MIN_VAL, message = LONGITUDE_ERR_MSG)
                                                        @DecimalMax(value = LONGITUDE_MAX_VAL, message = LONGITUDE_ERR_MSG)
                                                                double lon1,
                                                        @RequestParam
                                                        @DecimalMin(value = LATITUDE_MIN_VAL, message = LATITUDE_ERR_MSG)
                                                        @DecimalMax(value = LATITUDE_MAX_VAL, message = LATITUDE_ERR_MSG)
                                                                double lat2,
                                                        @RequestParam
                                                        @DecimalMin(value = LONGITUDE_MIN_VAL, message = LONGITUDE_ERR_MSG)
                                                        @DecimalMax(value = LONGITUDE_MAX_VAL, message = LONGITUDE_ERR_MSG)
                                                                double lon2) {
        return vehicleService.findVehiclesWithinRectangle(lat1, lon1, lat2, lon2);
    }

    @PostMapping
    public VehicleResponseDto createVehicle(@RequestBody final VehicleRequestDto vehicleRequestDto) {
        return vehicleService.save(vehicleRequestDto);
    }

}
