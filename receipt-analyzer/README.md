# Receipt Analyzer

A Spring Boot application to fetch emails, identify receipts using AI, and speak the summary.

## Setup

1.  **Ollama**: Install [Ollama](https://ollama.com/) and run `ollama run llama2`.
2.  **Email**: Enable IMAP on your email provider. If using Gmail, generate an [App Password](https://myaccount.google.com/apppasswords).
3.  **Config**: Edit `src/main/resources/application.properties`:
    ```properties
    spring.mail.username=your-email@gmail.com
    spring.mail.password=your-app-password
    ```

## Running

Run the `ReceiptAnalyzerApplication` class from your IDE.

## Usage

Visit `http://localhost:8080/process-receipts` to trigger the analysis.
