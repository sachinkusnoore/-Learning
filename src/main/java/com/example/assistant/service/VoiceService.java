package com.example.assistant.service;

import org.springframework.stereotype.Service;

@Service
public class VoiceService {

    public void speak(String text) {
        // In a real implementation, this would use a TTS library.
        // For now, we print to console and maybe play a system beep.
        System.out.println(">>> VOICE REMINDER: " + text);
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
}
