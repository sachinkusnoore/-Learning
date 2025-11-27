package com.example.assistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class GmailVoiceAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(GmailVoiceAssistantApplication.class, args);
    }
}
