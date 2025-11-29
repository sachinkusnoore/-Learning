import pandas as pd
import os

class ReportGenerator:
    def __init__(self, output_file="email_summary_report.csv"):
        self.output_file = output_file

    def generate_report(self, processed_emails):
        # processed_emails is a list of dicts with keys: id, subject, sender, date, summary, category
        df = pd.DataFrame(processed_emails)
        
        # Ensure directory exists
        os.makedirs(os.path.dirname(self.output_file) if os.path.dirname(self.output_file) else '.', exist_ok=True)
        
        df.to_csv(self.output_file, index=False)
        print(f"Report generated at {self.output_file}")
        
        # Also print a quick summary to console
        print("\n--- Execution Summary ---")
        print(df[['subject', 'category', 'summary']].head())
