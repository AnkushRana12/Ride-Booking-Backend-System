package com.rideshare.matching_service.service;

import com.rideshare.matching_service.event.RideRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class RideEventConsumer {
    private final MatchingService matchingService;

    //Listen to ride.requested Topic
    //Trigger every time Ride Service Publish A new Ride Request

    //FLOW

    //RIDE SERVICE->KAFKA(RIDE.REQUESTED) ->THIS CONSUMES -> MATCHING SERVICE

//    @KafkaListener(
//            topics = "ride.requested",
//            groupId = "matching-service-group"
//    )
//    public void consumeRideRequestedEvent(RideRequestedEvent event){
//        try {
//            matchingService.matchDriverForRide(event);
//        }catch (Exception e){
//         log.error("Error processing ride request: {} - {}",
//                 event.getRideId(),e.getMessage());
//
//         //In Prodeuction send to dead letter queue for retry
//        }
//    }
@KafkaListener(
        topics = "ride.requested",
        groupId = "matching-service-group"
)
public void consumeRideRequestedEvent(RideRequestedEvent event) {

    log.info("Received RideRequestedEvent: {}", event);

    try {
        matchingService.matchDriverForRide(event);
    } catch (Exception e) {
        log.error("Error processing ride request", e);
    }
}

}
