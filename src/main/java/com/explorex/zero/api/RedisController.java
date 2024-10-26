package com.explorex.zero.api;

import com.explorex.zero.util.RedisUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RedisController {

    @Autowired
    private RedisUtils redisUtils;

    @GetMapping("/set-key")
    public String setKey() {
        try {
            redisUtils.set("testKey", "testValue");
            return "Key set successfully.";
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to set key: " + e.getMessage();
        }
    }

    @GetMapping("/get-key")
    public String getKey() {
        try {
            Object value = redisUtils.get("testKey");
            return "Value for testKey: " + value;
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to get key: " + e.getMessage();
        }
    }

    @GetMapping("/delete-key")
    public String deleteKey() {
        try {
            boolean deleted = redisUtils.delete("testKey");
            if (deleted) {
                return "Key deleted successfully.";
            } else {
                return "Key does not exist or failed to delete.";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Failed to delete key: " + e.getMessage();
        }
    }
}