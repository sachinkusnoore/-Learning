package com.example.receiptanalyzer.service;

import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

@Service
public class AnalysisService {

    private final ChatClient chatClient;

    public AnalysisService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String analyzeEmail(String emailContent) {
        String promptText = """
                Analyze the following email content.
                1. Determine if it is a receipt or invoice.
                2. If it is a receipt, categorize it into one of these categories: Utility, Expenses, Entertainment.
                3. Extract the total amount if possible.
                4. Provide a brief summary for voice output.
                
                Return the result in JSON format with the following keys:
                - isReceipt (boolean)
                - category (String, one of Utility, Expenses, Entertainment, or Other)
                - amount (String)
                - voiceSummary (String, a short sentence to be spoken)
                
                Email Content:
                %s
                """.formatted(emailContent);

        try {
            return chatClient.call(promptText);
        } catch (Exception e) {
            return "{\"isReceipt\": false, \"error\": \"Failed to analyze\"}";
        }
    }
}
