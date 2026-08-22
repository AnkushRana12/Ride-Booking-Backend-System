package com.rideshare.ride_service.service;

import com.rideshare.ride_service.dto.RideRequest;
import com.rideshare.ride_service.dto.RideResponse;
import com.rideshare.ride_service.event.RideRequestEvent;
import com.rideshare.ride_service.model.Ride;
import com.rideshare.ride_service.model.RideStatus;
import com.rideshare.ride_service.repository.RideRepository;
import lombok.AllArgsConstructor;
//import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
@Slf4j
public class RideService {
    private final RideRepository rideRepository;
    private final KafkaTemplate<String, RideRequestEvent> kafkaTemplate;

    private static final String RIDE_REQUEST_TOPIC = "ride.requested";

    // CREATE RIDE IN DB WITH REQUESTED STATUS

    public RideResponse requestRide(RideRequest rideRequest) {
        log.info("New ride request from rider: {}", rideRequest.getRiderid());

        //STEP 1 :SAVE RIDE TO DATABASE
        Ride ride = new Ride();
        ride.setRiderId(rideRequest.getRiderid());
        ride.setPickupLatitude(rideRequest.getPickupLatitude());
        ride.setPickupLongitude(rideRequest.getPickupLongitude());
        ride.setPickupAddress(rideRequest.getPickupAddress());

        ride.setDropLatitude(rideRequest.getDropLatitude());
        ride.setDropLongitude(rideRequest.getDropLongitude());
        ride.setDropAddress(rideRequest.getDropAddress());

        ride.setStatus(RideStatus.REQUESTED);
        ride.setEstimatedFare(calculateEstimatedFare(rideRequest));
        Ride savedRide = rideRepository.save(ride);

        //STEP 2 :PUBLISH EVENT TO KAFKA
        // MATCHING SERVICE WILL CONSUME this and find the nearest driver
        RideRequestEvent event = new RideRequestEvent(
                savedRide.getId(),
                savedRide.getRiderId(),
                savedRide.getPickupLatitude(),
                savedRide.getPickupLongitude(),
                savedRide.getPickupAddress(),
                savedRide.getDropLatitude(),
                savedRide.getDropLongitude(),
                savedRide.getDropAddress()
        );
        kafkaTemplate.send(RIDE_REQUEST_TOPIC, savedRide.getId(), event);
        log.info("RideRequestedEvent published to Kafka for ride :{}", savedRide.getId());

        //update status to matching

        savedRide.setStatus(RideStatus.MATCHING);
        rideRepository.save(savedRide);
        return mapToResponse(savedRide);
    }


    public void updateRideWithDriver(String rideId, String driverId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setDriverId(driverId);
        ride.setStatus(RideStatus.ACCEPTED);
        rideRepository.save(ride);
    }


    public RideResponse startRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        if (ride.getStatus() != RideStatus.ACCEPTED) {
            throw new RuntimeException("Ride cannot be started.Current status :  " + ride.getStatus());
        } else {
            ride.setStatus(RideStatus.RIDE_STARTED);
            ride.setStartedAt(LocalDateTime.now());
            rideRepository.save(ride);
        }
        return mapToResponse(ride);

    }


    public RideResponse completeRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));

        if (ride.getStatus() != RideStatus.RIDE_STARTED) {
            throw new RuntimeException("Ride cannot be completed .Current Status: " + ride.getStatus());
        }
        ride.setStatus(RideStatus.COMPLETED);
        ride.setCompletedAt(LocalDateTime.now());
        ride.setActualFare(ride.getEstimatedFare());
        rideRepository.save(ride);
        return mapToResponse(ride);
    }


    public RideResponse cancelRide(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        ride.setStatus(RideStatus.CANCELLED);
        rideRepository.save(ride);
        return mapToResponse(ride);
    }


    public RideResponse getRideById(String rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new RuntimeException("Ride not found"));
        return mapToResponse(ride);
    }


    public List<RideResponse> getRidesByRider(String riderId) {
        return rideRepository.findByRiderIdOrderByCreatedAtDesc(riderId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }



    private double calculateEstimatedFare(RideRequest request){
//simplified Havesine Distance Formula//

        double lat1=Math.toRadians(request.getPickupLatitude());
        double lat2=Math.toRadians(request.getDropLatitude());

        double lon1=Math.toRadians(request.getPickupLongitude());
        double lon2=Math.toRadians(request.getDropLongitude());

        double dLat=lat2-lat1;
        double dLon=lon2-lon1;

        double a = Math.pow(Math.sin(dLat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dLon / 2), 2);

        double c = 2 * Math.asin(Math.sqrt(a));

        double distanceKm = 6371 * c;

// Base fare: 50Rs + 12Rs. perKm
        double fare = 50 + (distanceKm * 12);

        return Math.round(fare * 100.0) / 100.0;

    }


    private RideResponse mapToResponse(Ride ride) {

        RideResponse response = new RideResponse();

        response.setId(ride.getId());
        response.setRiderId(ride.getRiderId());
        response.setDriverId(ride.getDriverId());

        response.setPickupLatitude(ride.getPickupLatitude());
        response.setPickupLongitude(ride.getPickupLongitude());
        response.setPickupAddress(ride.getPickupAddress());

        response.setDropLatitude(ride.getDropLatitude());
        response.setDropLongitude(ride.getDropLongitude());
        response.setDropAddress(ride.getDropAddress());

        response.setStatus(ride.getStatus());

        response.setEstimatedFare(ride.getEstimatedFare());
        response.setActualFare(ride.getActualFare());

        response.setCreatedAt(ride.getCreatedAt());
        response.setStartedAt(ride.getStartedAt());
        response.setCompletedAt(ride.getCompletedAt());

        return response;
    }

}
