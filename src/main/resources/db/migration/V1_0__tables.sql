CREATE TABLE `vehicle` (
     `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
     `description` VARCHAR(255) NULL,
     PRIMARY KEY (`id`)
);

CREATE TABLE `vehicle_location` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
    `location` POINT NOT NULL SRID 4326,
    `registered` TIMESTAMP(0) NOT NULL,
    `vehicle_id` BIGINT(20) NOT NULL,
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_vehicle_location_to_vehicle` FOREIGN KEY (`vehicle_id`) REFERENCES `vehicle` (`id`)
);

CREATE SPATIAL INDEX `si_vehicle_location_location` ON `vehicle_location` (`location`);