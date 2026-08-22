package com.rideshare.ride_service.model;

import jakarta.persistence.Enumerated;
//Flow
// Requested -> Matching ->Accepted -> DRIVER_ARRIVING
//           -> RIDE_STARTED -> COMPLETED
//              CANCELLED(can happen at multiple stages)
public enum RideStatus {
    REQUESTED ,
    MATCHING,
    ACCEPTED ,
    DRIVER_ARRIVING,
    RIDE_STARTED,
    COMPLETED,
    CANCELLED

}
