package com.example.assistant.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import org.springframework.stereotype.Service;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class GmailService {

    private static final String APPLICATION_NAME = "Gmail Voice Assistant";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final String TOKENS_DIRECTORY_PATH = "tokens";
    private static final List<String> SCOPES = Collections.singletonList(GmailScopes.GMAIL_READONLY);
    private static final String CREDENTIALS_FILE_PATH = "/credentials.json";

    private Gmail service;

    public GmailService() {
        try {
            this.service = getGmailService();
        } catch (Exception e) {
            System.err.println("Failed to initialize Gmail Service: " + e.getMessage());
            // In a real app, handle this better (maybe retry or fail startup)
        }
    }

    private Gmail getGmailService() throws Exception {
        final NetHttpTransport HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        InputStream in = GmailService.class.getResourceAsStream(CREDENTIALS_FILE_PATH);
        if (in == null) {
            throw new FileNotFoundException("Resource not found: " + CREDENTIALS_FILE_PATH);
        }
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY, new InputStreamReader(in));

        GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                HTTP_TRANSPORT, JSON_FACTORY, clientSecrets, SCOPES)
                .setDataStoreFactory(new FileDataStoreFactory(new java.io.File(TOKENS_DIRECTORY_PATH)))
                .setAccessType("offline")
                .build();
        LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
        Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

        return new Gmail.Builder(HTTP_TRANSPORT, JSON_FACTORY, credential)
                .setApplicationName(APPLICATION_NAME)
                .build();
    }

    public List<String> fetchRecentEmails() {
        if (service == null) {
            System.out.println("Gmail service not initialized (missing credentials?)");
            return Collections.emptyList();
        }

        List<String> emailBodies = new ArrayList<>();
        try {
            // List messages.
            ListMessagesResponse response = service.users().messages().list()
                    .setUserId("me")
                    .setMaxResults(10L) // Fetch last 10 emails
                    .execute();

            List<Message> messages = response.getMessages();
            if (messages == null || messages.isEmpty()) {
                System.out.println("No messages found.");
            } else {
                for (Message message : messages) {
                    Message fullMessage = service.users().messages().get("me", message.getId()).execute();
                    String snippet = fullMessage.getSnippet(); // Simple snippet for now
                    // Ideally, we parse the payload for full body, but snippet is often enough for appointments
                    emailBodies.add(snippet);
                }
            }
        } catch (IOException e) {
            System.err.println("Error fetching emails: " + e.getMessage());
        }
        return emailBodies;
    }
}
