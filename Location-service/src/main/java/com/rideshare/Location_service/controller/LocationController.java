package com.rideshare.Location_service.controller;

import com.rideshare.Location_service.dto.DriverLocatinonRequest;
import com.rideshare.Location_service.dto.NearByDriverResponse;
import com.rideshare.Location_service.service.LocationService;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
@Slf4j
@RequiredArgsConstructor
public class LocationController {
    private final LocationService locationService;

    @PostMapping("/driver/update")//----Driver phone call this every 3 sec----//
    public ResponseEntity<String> updateDriverLocation(@RequestBody DriverLocatinonRequest driverLocatinonRequest){
        locationService.updateDriverLocation(driverLocatinonRequest);

        System.out.println("hello bhai");
        return ResponseEntity.ok("Driver Location Updatedd");
    }

    @GetMapping("/drivers/nearby")//----Matching service calls this ride is requested---//
    public ResponseEntity<List<NearByDriverResponse>>getNearByDrivers(@RequestParam double latitude,
                                                                      @RequestParam double longitude,
                                                                      @RequestParam(defaultValue = "5.0")double radius){
        return ResponseEntity.ok(locationService.findNearByDrivers(latitude,longitude,radius));
    }

    //----when driver goes offline---//
    @DeleteMapping("/drivers/{driverId}")
    public ResponseEntity<String> removeDriver(@PathVariable String driverId){
        locationService.removeDriver(driverId);
        return ResponseEntity.ok("Driver removed successfully");
    }
}
