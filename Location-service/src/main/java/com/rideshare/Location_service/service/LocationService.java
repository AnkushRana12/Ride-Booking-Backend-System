package com.rideshare.Location_service.service;

import com.rideshare.Location_service.dto.DriverLocatinonRequest;
import com.rideshare.Location_service.dto.NearByDriverResponse;
//import io.lettuce.core.api.sync.RedisGeoCommands;
import org.springframework.data.redis.connection.RedisGeoCommands;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.geo.*;
import org.springframework.data.redis.core.RedisTemplate;

import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.connection.RedisGeoCommands.GeoLocation;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class LocationService {

    private final RedisTemplate<String,String>redisTemplate;

    //Redis Key For All The Driver Locations
    private static final String DRIVER_GEO_KEY="drivers:locations";


    public void updateDriverLocation(DriverLocatinonRequest driverLocatinonRequest){
        log.info("Updating location for driver:{}",driverLocatinonRequest.getDriverid());

        //longitude first ,then latitude -GeoSpatial Standard//
        Point driverPoint=new Point(
                driverLocatinonRequest.getLongitude(),
                driverLocatinonRequest.getLatitude()

        );
   redisTemplate.opsForGeo().add(//udate the location
           DRIVER_GEO_KEY,driverPoint, driverLocatinonRequest.getDriverid()
   );
   log.info("location updated for driver:{}",driverLocatinonRequest.getDriverid());
    }


    //find near by drivers with given radius
    //called by matching service  on ride service
    //Maps to Radis FOR EADIUS COMMANDS

    public List<NearByDriverResponse> findNearByDrivers(
            double latitude,double longitude,double rediusInKm
    ){
        log.info("Finidng drivers near by lat:{} long:{} within{}km",latitude,longitude,rediusInKm);

        Circle searchArea=new Circle(
                new Point(longitude,latitude),
                new Distance(rediusInKm, Metrics.KILOMETERS)
        );
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        DRIVER_GEO_KEY,
                        searchArea,
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs()
                                .includeDistance()
                                .includeCoordinates()
                                .sortAscending()
                                .limit(10)
                );
        List<NearByDriverResponse> nearbyDrivers=new ArrayList<>();
        if(results!=null){
            results.getContent().forEach(result->{
                RedisGeoCommands.GeoLocation<String> location=result.getContent();
                nearbyDrivers.add(new NearByDriverResponse(
                        location.getName()
                        ,location.getPoint().getY(),
                        location.getPoint().getX(),
                        result.getDistance().getValue()
                ));
            });
        }
   log.info("Foundd {} drivers nearby",nearbyDrivers.size());
   return nearbyDrivers;
    }



    //Remove Driver when they offline
    //Maps to Radis ZREM commands

    public  void removeDriver(String driverId){
        log.info("Removing driver :{}",driverId);
        redisTemplate.opsForGeo().remove(DRIVER_GEO_KEY,driverId);
    }
}
