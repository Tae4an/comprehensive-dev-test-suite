package com.example.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/api")
@Slf4j
public class TestController {
    
    @GetMapping("/log-test")
    public String test() {
        log.info("=== Test Start ===");
        log.debug("Debug level test message");
        log.info("Info level test message with data: {}", new Date());
        log.warn("Warning level test message");
        return "test";
    }
}