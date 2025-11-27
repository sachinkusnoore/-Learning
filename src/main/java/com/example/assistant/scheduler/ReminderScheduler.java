package com.example.assistant.scheduler;

import com.example.assistant.model.Appointment;
import com.example.assistant.service.GmailService;
import com.example.assistant.service.RagService;
import com.example.assistant.service.VoiceService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class ReminderScheduler {

    private final GmailService gmailService;
    private final RagService ragService;
    private final VoiceService voiceService;
    private final Set<Appointment> remindedAppointments = new HashSet<>();

    public ReminderScheduler(GmailService gmailService, RagService ragService, VoiceService voiceService) {
        this.gmailService = gmailService;
        this.ragService = ragService;
        this.voiceService = voiceService;
    }

    // Check every 5 minutes
    @Scheduled(fixedRate = 300000)
    public void checkAppointments() {
        System.out.println("Checking for appointments...");
        List<String> emails = gmailService.fetchRecentEmails();
        List<Appointment> appointments = ragService.extractAppointments(emails);

        LocalDateTime now = LocalDateTime.now();
        for (Appointment appt : appointments) {
            if (appt.dateTime() == null) continue;

            long minutesUntil = Duration.between(now, appt.dateTime()).toMinutes();
            
            // Remind if between 55 and 65 minutes away, and haven't reminded yet
            if (minutesUntil >= 55 && minutesUntil <= 65 && !remindedAppointments.contains(appt)) {
                String message = "You have an appointment coming up: " + appt.subject() + " at " + appt.dateTime().toLocalTime();
                voiceService.speak(message);
                remindedAppointments.add(appt);
            }
        }
    }
}
