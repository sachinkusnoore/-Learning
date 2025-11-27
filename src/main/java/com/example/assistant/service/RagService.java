package com.example.assistant.service;

import com.example.assistant.model.Appointment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.service.UserMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class RagService {

    interface AppointmentExtractor {
        @UserMessage("Extract all appointments from the following email text. If none, return null. Current time is {{currentTime}}.\n\nEmail: {{emailText}}")
        Appointment extract(String emailText, String currentTime);
    }

    private final AppointmentExtractor extractor;

    public RagService(@Value("${openai.api.key:demo}") String apiKey) {
        // Using OpenAI for now. User needs to provide key in application.properties or env var.
        ChatLanguageModel model = OpenAiChatModel.withApiKey(apiKey);
        this.extractor = AiServices.create(AppointmentExtractor.class, model);
    }

    public List<Appointment> extractAppointments(List<String> emailBodies) {
        List<Appointment> appointments = new ArrayList<>();
        String now = LocalDateTime.now().toString();
        
        for (String body : emailBodies) {
            try {
                Appointment appointment = extractor.extract(body, now);
                if (appointment != null) {
                    appointments.add(appointment);
                }
            } catch (Exception e) {
                System.err.println("Failed to extract from email: " + e.getMessage());
            }
        }
        return appointments;
    }
}
