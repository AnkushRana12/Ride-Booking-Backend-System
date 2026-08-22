package com.rideshare.ride_service.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequest {
    @NotBlank(message = "Ride id is required")
    private String riderid;
    @NotNull(message = "pickup latitude is required " )
    private double pickupLatitude;
    @NotNull(message = "pickup longitude is required ")
    private double pickupLongitude;
    @NotNull(message = "pickup Address is required")
    private String pickupAddress;
    @NotNull(message = "drop latitude is required")
    private double dropLatitude;
    @NotNull(message = "drop longitude is required")
    private double dropLongitude;
    @NotNull(message = "drop address is required")
    private String dropAddress;
}
