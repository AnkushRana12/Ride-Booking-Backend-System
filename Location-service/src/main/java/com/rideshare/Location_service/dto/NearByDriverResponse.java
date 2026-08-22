package com.rideshare.Location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NearByDriverResponse {
    private String driverid;
    private double latitude;
    private double longitude;
    private double distanceInKm;
}
