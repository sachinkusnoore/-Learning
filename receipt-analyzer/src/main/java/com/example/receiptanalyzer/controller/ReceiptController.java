package com.example.receiptanalyzer.controller;

import com.example.receiptanalyzer.service.AnalysisService;
import com.example.receiptanalyzer.service.EmailService;
import com.example.receiptanalyzer.service.VoiceService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReceiptController {

    private final EmailService emailService;
    private final AnalysisService analysisService;
    private final VoiceService voiceService;
    private final ObjectMapper objectMapper;

    @GetMapping("/process-receipts")
    public String processReceipts() {
        List<String> emails = emailService.fetchUnreadEmails();
        StringBuilder resultLog = new StringBuilder();

        for (String email : emails) {
            String analysisJson = analysisService.analyzeEmail(email);
            resultLog.append("Analyzed: ").append(analysisJson).append("\n");

            try {
                JsonNode root = objectMapper.readTree(analysisJson);
                if (root.has("isReceipt") && root.get("isReceipt").asBoolean()) {
                    String summary = root.has("voiceSummary") ? root.get("voiceSummary").asText() : "Receipt found.";
                    String category = root.has("category") ? root.get("category").asText() : "Unknown";
                    
                    String speech = "Found a " + category + " receipt. " + summary;
                    voiceService.speak(speech);
                }
            } catch (Exception e) {
                System.err.println("Error parsing JSON: " + e.getMessage());
            }
        }
        
        if (emails.isEmpty()) {
            return "No unread emails found.";
        }

        return resultLog.toString();
    }
}
