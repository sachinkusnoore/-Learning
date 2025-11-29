package com.example.receiptanalyzer.service;

import org.springframework.stereotype.Service;
import java.io.IOException;

@Service
public class VoiceService {

    public void speak(String text) {
        String cleanText = text.replace("\"", "'").replace("\n", " ");
        // PowerShell command to speak text using System.Speech
        String command = String.format(
                "powershell -Command \"Add-Type -AssemblyName System.Speech; (New-Object System.Speech.Synthesis.SpeechSynthesizer).Speak('%s')\"",
                cleanText
        );

        try {
            Process process = Runtime.getRuntime().exec(command);
            process.waitFor();
        } catch (IOException | InterruptedException e) {
            System.err.println("Failed to speak text: " + e.getMessage());
        }
    }
}
