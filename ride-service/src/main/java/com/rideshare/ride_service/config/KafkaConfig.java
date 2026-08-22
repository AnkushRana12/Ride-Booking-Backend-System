package com.rideshare.ride_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {
    //Topic where Ride Service Publish Ride Request
    //Matching service subscribe  to this topic

    @Bean
    public NewTopic rideRequestedTopic(){
        return TopicBuilder.name("ride.requested")
                .partitions(3)
                .replicas(1)
                .build();
    }

    //TOPIC WHERE MATCHING SERVICE PUBLISH MATCH RESULT
    //RIDE SERVICE SUBSCRIBER TO THIS TOPIC

    @Bean
    public  NewTopic rideMatchedTopic(){
        return TopicBuilder.name("ride.matched")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
