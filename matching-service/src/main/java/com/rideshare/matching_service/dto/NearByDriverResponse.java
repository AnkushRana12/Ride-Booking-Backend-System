package com.rideshare.matching_service.dto;


//Response recieved from location setvice
//when query for near by drivers

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
