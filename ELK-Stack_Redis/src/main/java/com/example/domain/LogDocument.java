package com.example.domain;

import lombok.Data;

@Data
public class LogDocument {
    private String id;
    private String timestamp;
    private String level;
    private String logger;
    private String message;
    private String thread;
    private String application;
}
