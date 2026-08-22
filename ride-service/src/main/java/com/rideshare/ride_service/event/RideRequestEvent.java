package com.rideshare.ride_service.event;

//EVENT PUBLISHED TO KAFKA when a ride is requested
// Matching service consumes this event
//topic:ride.request

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RideRequestEvent {

    private String rideId;
    private String riderId;


//pickup
    private double pickupLatitude;

    private double pickupLongitude;

    private String pickupAddress;

    //drop
    private double dropLatitude;

    private double dropLongitude;

    private String dropAddress;

}
