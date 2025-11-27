# Gmail RAG Voice Assistant

This is a Spring Boot application that:
1.  Fetches emails from your Gmail account.
2.  Uses AI (RAG) to find upcoming appointments.
3.  Announces a voice reminder 1 hour before the appointment.

## Prerequisites

1.  **Java 17+** installed.
2.  **Gradle** installed (or use your IDE's Gradle wrapper).
3.  **OpenAI API Key**.
4.  **Google Cloud Credentials**.

## Setup

### 1. Google Credentials
1.  Go to the [Google Cloud Console](https://console.cloud.google.com/).
2.  Create a project and enable the **Gmail API**.
3.  Create **OAuth 2.0 Client IDs** (Desktop app).
4.  Download the JSON file, rename it to `credentials.json`, and place it in:
    `src/main/resources/credentials.json`

### 2. OpenAI API Key
Open `src/main/resources/application.properties` and update the key:
```properties
openai.api.key=YOUR_ACTUAL_API_KEY
```

### 3. Run the Application
If you have Gradle installed:
```bash
gradle bootRun
```
Or open the project in IntelliJ IDEA / Eclipse and run `GmailVoiceAssistantApplication`.

## Usage
- The app runs a background scheduler every 5 minutes.
- To manually trigger a check, you can send a POST request:
  ```bash
  curl -X POST http://localhost:8080/check-now
  ```
- Watch the console for "VOICE REMINDER" logs (and a system beep).
