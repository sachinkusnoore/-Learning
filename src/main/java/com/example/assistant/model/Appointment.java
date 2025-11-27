package com.example.assistant.model;

import java.time.LocalDateTime;

public record Appointment(String subject, LocalDateTime dateTime) {
}
