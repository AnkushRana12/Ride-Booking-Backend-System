package com.rideshare.Location_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data //use for getter settter string etc
@AllArgsConstructor
@NoArgsConstructor
public class DriverLocatinonRequest {
    private String driverid;
    private double latitude;
    private double longitude;
}
