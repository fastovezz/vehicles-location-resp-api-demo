package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

import static com.example.demo.util.Constants.LATITUDE_ERR_MSG;
import static com.example.demo.util.Constants.LATITUDE_MAX_VAL;
import static com.example.demo.util.Constants.LATITUDE_MIN_VAL;
import static com.example.demo.util.Constants.LONGITUDE_ERR_MSG;
import static com.example.demo.util.Constants.LONGITUDE_MAX_VAL;
import static com.example.demo.util.Constants.LONGITUDE_MIN_VAL;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleLocationDto {
    @DecimalMin(value = LATITUDE_MIN_VAL, message = LATITUDE_ERR_MSG)
    @DecimalMax(value = LATITUDE_MAX_VAL, message = LATITUDE_ERR_MSG)
    private double latitude;

    @DecimalMin(value = LONGITUDE_MIN_VAL, message = LONGITUDE_ERR_MSG)
    @DecimalMax(value = LONGITUDE_MAX_VAL, message = LONGITUDE_ERR_MSG)
    private double longitude;

    @NotNull
    private ZonedDateTime registered;

    @NotNull
    private Long vehicleId;

}
