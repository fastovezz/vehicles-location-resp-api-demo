package com.example.demo;

import com.example.demo.dto.VehicleLocationDto;
import com.example.demo.dto.VehicleRequestDto;
import com.example.demo.dto.VehicleResponseDto;
import com.example.demo.dto.VehicleWithLocationDto;
import com.example.demo.dto.VehiclesCollectionDto;
import com.example.demo.utils.DatabaseCleanupService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.ResponseEntity;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class DemoApplicationTests {
    @LocalServerPort
    private int port;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private DatabaseCleanupService truncateDatabaseService;

    @Test
    public void testLocatingVehiclesWithinRectangle() {
        List<VehicleLocationRegistrationData> dataList =
                Arrays.asList(VehicleLocationRegistrationData.create("description1", ZonedDateTime.now(ZoneId.of("UTC")), 48.05, 31.05), // in the middle of rectangle
                        VehicleLocationRegistrationData.create("description2", ZonedDateTime.now(ZoneId.of("UTC")).minusDays(1), 48.05, 31.05), // in the middle of rectangle but seen a day ago
                        VehicleLocationRegistrationData.create("description3", ZonedDateTime.now(ZoneId.of("UTC")), 48.099999, 31.07), // near the edge of rectangle
                        VehicleLocationRegistrationData.create("description4", ZonedDateTime.now(ZoneId.of("UTC")), 48.099999, 31.099999), // near the angle of rectangle
                        VehicleLocationRegistrationData.create("description5", ZonedDateTime.now(ZoneId.of("UTC")), 48.15, 31.03), // outside the rectangle
                        VehicleLocationRegistrationData.create("description6", ZonedDateTime.now(ZoneId.of("UTC")), 47.95, 31.08)); // outside the rectangle

        for (VehicleLocationRegistrationData data : dataList) {
            VehicleResponseDto vehicleResponseDto = registerVehicle(data.description);

            long vehicleId = vehicleResponseDto.getId();
            registerVehicleLocation(vehicleId, data.registered, data.latitude, data.longitude, VehicleLocationDto.class);
        }


        VehiclesCollectionDto vehiclesCollectionDto = restTemplate.getForObject("http://localhost:" + port + "/vehicles?lat1=48&lon1=31&lat2=48.1&lon2=31.1", VehiclesCollectionDto.class);
        assertThat(vehiclesCollectionDto.getVehicles().size()).isEqualTo(3);
        assertThat(vehiclesCollectionDto.getVehicles().stream()
                .map(VehicleWithLocationDto::getVehicle)
                .map(VehicleResponseDto::getDescription)
                .collect(Collectors.toList()).containsAll(Arrays.asList("description1", "description3", "description4"))).isTrue();
    }

    private VehicleResponseDto registerVehicle(String description) {
        final String url = "http://localhost:" + port + "/vehicles";
        final VehicleRequestDto vehicleRequestDto = VehicleRequestDto.builder()
                .description(description)
                .build();
        VehicleResponseDto vehicleResponseDto = restTemplate.postForObject(url, vehicleRequestDto, VehicleResponseDto.class);
        assertThat(vehicleResponseDto.getDescription()).isEqualTo(description);
        return vehicleResponseDto;
    }

    private <T> ResponseEntity<T> registerVehicleLocation(long vehicleId, ZonedDateTime registeredTime, double latitude, double longitude, Class<T> responseType) {
        final String url = "http://localhost:" + port + "/vehicles/" + vehicleId + "/locations";
        final VehicleLocationDto vehicleLocationDto = VehicleLocationDto.builder()
                .vehicleId(vehicleId)
                .registered(registeredTime)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        return restTemplate.postForEntity(url, vehicleLocationDto, responseType);
    }

    @ParameterizedTest
    @MethodSource("provideQueryStringsForSearchWithinRectangle")
    public void testQueryStringsValidationForSearchWithinRectangle(String queryString, int expectedHttpCode) {
        ResponseEntity<Object> responseEntity = restTemplate.getForEntity("http://localhost:" + port + "/vehicles?" + queryString, Object.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(expectedHttpCode);
    }

    private static Stream<Arguments> provideQueryStringsForSearchWithinRectangle() {
        return Stream.of(
                Arguments.of("lat1=-91&lon1=-180&lat2=90&lon2=180", 400),
                Arguments.of("lat1=-90&lon1=-180&lat2=91&lon2=180", 400),
                Arguments.of("lat1=-90&lon1=-181&lat2=90&lon2=180", 400),
                Arguments.of("lat1=-90&lon1=-180&lat2=90&lon2=181", 400),
                Arguments.of("lat1=-90&lon1=-180&lat2=90&lon2=180", 200),
                Arguments.of("lat1=-45&lon1=-90&lat2=45&lon2=90", 200)
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForRegisteringVehicleLocation")
    public void testDtosValidationForRegisteringVehicleLocation(double latitude, double longitude,
                                                                ZonedDateTime registeredDateTime, int expectedHttpCode) {
        VehicleResponseDto vehicleResponseDto = registerVehicle("description");

        long vehicleId = vehicleResponseDto.getId();
        ResponseEntity<Object> responseEntity = registerVehicleLocation(vehicleId, registeredDateTime, latitude, longitude, Object.class);
        assertThat(responseEntity.getStatusCodeValue()).isEqualTo(expectedHttpCode);
    }

    private static Stream<Arguments> provideDataForRegisteringVehicleLocation() {
        return Stream.of(
                Arguments.of(-91, -180, ZonedDateTime.now(ZoneId.of("UTC")), 400),
                Arguments.of(-90, -181, ZonedDateTime.now(ZoneId.of("UTC")), 400),
                Arguments.of(91, 180, ZonedDateTime.now(ZoneId.of("UTC")), 400),
                Arguments.of(90, 181, ZonedDateTime.now(ZoneId.of("UTC")), 400),
                Arguments.of(-90, -180, ZonedDateTime.now(ZoneId.of("UTC")), 200),
                Arguments.of(90, 180, ZonedDateTime.now(ZoneId.of("UTC")), 200),
                Arguments.of(90, 180, null, 400)
        );
    }

    @AfterEach
    public void cleanupAfterEach() {
        truncateDatabaseService.truncate();
    }

    static public class VehicleLocationRegistrationData {
        public VehicleLocationRegistrationData(String description, ZonedDateTime registered, double latitude, double longitude) {
            this.description = description;
            this.registered = registered;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public String description;
        public ZonedDateTime registered;
        public double latitude;
        public double longitude;

        public static VehicleLocationRegistrationData create(String description, ZonedDateTime registered, double latitude, double longitude) {
            return new VehicleLocationRegistrationData(description, registered, latitude, longitude);
        }
    }

}
