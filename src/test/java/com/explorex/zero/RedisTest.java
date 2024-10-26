package com.explorex.zero;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class RedisTest {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Test
    void contextLoads() {
    }

    @Test
    void testRedisConnection() {
        String key = "test_key";
        String value = "test_value";

        redisTemplate.opsForValue().set(key, value);
        String retrievedValue = (String) redisTemplate.opsForValue().get(key);

        assertEquals(value, retrievedValue);
    }
}
