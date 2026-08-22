package com.rideshare.matching_service.service;

import com.rideshare.matching_service.client.LocationServiceClient;
import com.rideshare.matching_service.dto.NearByDriverResponse;
import com.rideshare.matching_service.event.RideMatchedEvent;
import com.rideshare.matching_service.event.RideRequestedEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@AllArgsConstructor
public class MatchingService {
    private final LocationServiceClient locationServiceClient;
    private final KafkaTemplate <String, RideMatchedEvent> kafkaTemplate;
    private static final String RIDE_MATCHED_TOPIC="ride.matched";
    private static double DEFAULT_SEARCH_RADIUS_KM =5.0;

    //Main MATCHING ALGORITH
    //CALLED WHEN RIDEREQUESTEVENT IS CONSUMED FROM KAFKA

   //STEPS:
    //1. ASK LOCATION SERVICE FOR NEARBY DRIVERS



    public void matchDriverForRide(RideRequestedEvent event){
        List<NearByDriverResponse>nearByDrivers=locationServiceClient.getNearByDrivers(
                event.getPickupLatitude(),
                event.getPickupLongitude(),
                DEFAULT_SEARCH_RADIUS_KM
        );
        if(nearByDrivers.isEmpty()){
            log.warn("No Drivers Found Near Ride :{}");
            return;
        }

        //if found
        //STEP 2.SCORE EACH DRIVER AND PICK THE BEST ONE //
        Optional<NearByDriverResponse> bestDriver=findBestDriver(nearByDrivers);

        if(bestDriver.isEmpty()){
            log.warn("could not find suitable driver for ride");
            return;
        }

        NearByDriverResponse assignedDriver=bestDriver.get();

        //STEP 3. PUBLISH RIDEMATCHED EVENT TO KAFKA
        RideMatchedEvent matchedEvent=new RideMatchedEvent(
                event.getRideId(),
                event.getRiderId(),
                assignedDriver.getDriverid(),
                assignedDriver.getLatitude(),
                assignedDriver.getLongitude(),
                assignedDriver.getDistanceInKm()
        );
        kafkaTemplate.send(RIDE_MATCHED_TOPIC,event.getRideId(),matchedEvent);
    log.info("RideMatchedEvent published");
    }

    //DRIVER SCORING ALGORITH
    //DISTACNE 70%
    //RATING 30 %

    //SCORE=>(1/DISTANCE)

    private Optional<NearByDriverResponse>findBestDriver(List<NearByDriverResponse> drivers){
    double distanceWeight=0.7;
    double ratingWeigth=0.3;
      return drivers.stream()
              .max(Comparator.comparingDouble(driver->{
                  //distance score =high score
                  //add 0.1 to avoid devision by 0
                  double distanceScore=1.0/(driver.getDistanceInKm()+0.1);

                  //simulated rating 4.0 and 5.0
                  //in production fetch from driver service

                  double simulatedRting =4.0+Math.random();

                  //final weighted score
                  return (distanceScore*distanceWeight)+
                          (simulatedRting*ratingWeigth);
              
              }));
    }

}
