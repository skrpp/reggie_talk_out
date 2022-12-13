package com.jt.reggie.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;


    @Configuration
    class RedisConfig {

//        @Bean
//        public JedisConnectionFactory redisConnectionFactory() {
//
//            return new JedisConnectionFactory(config);
//        }

        @Bean
        RedisTemplate<String, String> redisTemplate(RedisConnectionFactory redisConnectionFactory) {

            RedisTemplate<String, String> template = new RedisTemplate<>();
            template.setKeySerializer(new StringRedisSerializer());
            template.setConnectionFactory(redisConnectionFactory);
            return template;
        }
    }



