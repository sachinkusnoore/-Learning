import os.path
import base64
from google.auth.transport.requests import Request
from google.oauth2.credentials import Credentials
from google_auth_oauthlib.flow import InstalledAppFlow
from googleapiclient.discovery import build
from bs4 import BeautifulSoup

# If modifying these scopes, delete the file token.json.
SCOPES = ['https://www.googleapis.com/auth/gmail.modify']

class GmailService:
    def __init__(self):
        self.creds = None
        self.service = None
        self.authenticate()

    def authenticate(self):
        if os.path.exists('token.json'):
            self.creds = Credentials.from_authorized_user_file('token.json', SCOPES)
        if not self.creds or not self.creds.valid:
            if self.creds and self.creds.expired and self.creds.refresh_token:
                self.creds.refresh(Request())
            else:
                if not os.path.exists('credentials.json'):
                    raise FileNotFoundError("credentials.json not found. Please place it in the project root.")
                flow = InstalledAppFlow.from_client_secrets_file(
                    'credentials.json', SCOPES)
                self.creds = flow.run_local_server(port=0)
            # Save the credentials for the next run
            with open('token.json', 'w') as token:
                token.write(self.creds.to_json())

        self.service = build('gmail', 'v1', credentials=self.creds)

    def get_unread_emails(self, max_results=100):
        results = self.service.users().messages().list(userId='me', labelIds=['UNREAD'], maxResults=max_results).execute()
        messages = results.get('messages', [])
        emails = []

        if not messages:
            print('No unread messages found.')
            return []

        print(f"Found {len(messages)} unread messages.")
        for message in messages:
            msg = self.service.users().messages().get(userId='me', id=message['id']).execute()
            email_data = self._parse_email(msg)
            if email_data:
                emails.append(email_data)
        
        return emails

    def _parse_email(self, msg):
        headers = msg['payload']['headers']
        subject = next((h['value'] for h in headers if h['name'] == 'Subject'), 'No Subject')
        sender = next((h['value'] for h in headers if h['name'] == 'From'), 'Unknown Sender')
        date = next((h['value'] for h in headers if h['name'] == 'Date'), 'Unknown Date')
        
        body = ""
        if 'parts' in msg['payload']:
            for part in msg['payload']['parts']:
                if part['mimeType'] == 'text/plain':
                    if 'data' in part['body']:
                        body = base64.urlsafe_b64decode(part['body']['data']).decode('utf-8')
                        break
        elif 'body' in msg['payload'] and 'data' in msg['payload']['body']:
             body = base64.urlsafe_b64decode(msg['payload']['body']['data']).decode('utf-8')

        # Clean up body if it's HTML (simple approach)
        if not body and 'parts' in msg['payload']:
             for part in msg['payload']['parts']:
                if part['mimeType'] == 'text/html':
                    if 'data' in part['body']:
                        html_content = base64.urlsafe_b64decode(part['body']['data']).decode('utf-8')
                        soup = BeautifulSoup(html_content, 'html.parser')
                        body = soup.get_text()
                        break

        return {
            'id': msg['id'],
            'subject': subject,
            'sender': sender,
            'date': date,
            'body': body
        }

    def mark_as_read(self, email_id):
        self.service.users().messages().modify(userId='me', id=email_id, body={'removeLabelIds': ['UNREAD']}).execute()
        print(f"Marked email {email_id} as read.")
