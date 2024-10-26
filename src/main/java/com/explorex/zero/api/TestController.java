package com.explorex.zero.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test")
    public String test(@RequestParam(required = false) String flag) {
        return "Request processed: " + (flag == null ? "No flag" : flag);
    }
}