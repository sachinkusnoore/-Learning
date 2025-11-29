import os
import sys
from src.gmail_service import GmailService
from src.llm_service import LLMService
from src.vector_store import VectorStoreService
from src.report_generator import ReportGenerator

def main():
    print("Starting Email Summarization Application...")

    # 1. Initialize Services
    try:
        gmail_service = GmailService()
    except Exception as e:
        print(f"Error initializing Gmail Service: {e}")
        print("Please ensure 'credentials.json' is in the project root.")
        return

    try:
        llm_service = LLMService(model="llama3") # Ensure this model is pulled in Ollama
        vector_store = VectorStoreService(model="llama3")
        report_generator = ReportGenerator()
    except Exception as e:
         print(f"Error initializing other services: {e}")
         return

    # 2. Fetch Emails
    print("Fetching unread emails...")
    emails = gmail_service.get_unread_emails(max_results=100)
    
    if not emails:
        print("No emails to process.")
        return

    processed_emails = []

    # 3. Process Emails
    print(f"Processing {len(emails)} emails...")
    for email in emails:
        print(f"Processing email: {email['subject'][:30]}...")
        
        # Summarize
        summary = llm_service.summarize_email(email['body'])
        
        # Categorize
        category = llm_service.categorize_email(email['body'])
        
        # Store in Vector DB
        vector_store.add_email(email, summary, category)
        
        # Mark as Read
        gmail_service.mark_as_read(email['id'])
        
        # Collect data for report
        processed_emails.append({
            'id': email['id'],
            'subject': email['subject'],
            'sender': email['sender'],
            'date': email['date'],
            'summary': summary,
            'category': category
        })

    # 4. Generate Report
    report_generator.generate_report(processed_emails)
    print("Processing complete.")

if __name__ == "__main__":
    main()
