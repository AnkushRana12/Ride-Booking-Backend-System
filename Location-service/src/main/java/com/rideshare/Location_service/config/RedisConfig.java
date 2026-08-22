package com.rideshare.Location_service.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RedisConfig {
    public RedisTemplate<String,String> redisTemplate(
            RedisConnectionFactory connectionFactory){
        RedisTemplate<String,String>template=new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        //string serializers make data humanredable in redis CLI''
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        return template;
    }

}
