package com.example.assistant.controller;

import com.example.assistant.scheduler.ReminderScheduler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AssistantController {

    private final ReminderScheduler scheduler;

    public AssistantController(ReminderScheduler scheduler) {
        this.scheduler = scheduler;
    }

    @PostMapping("/check-now")
    public String checkNow() {
        scheduler.checkAppointments();
        return "Checked for appointments. Check console for details.";
    }
}
