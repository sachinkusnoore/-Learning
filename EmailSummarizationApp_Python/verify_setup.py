import sys
import os

# Add current directory to path
sys.path.append(os.getcwd())

def test_imports():
    print("Testing imports...")
    try:
        import googleapiclient
        import langchain
        import chromadb
        import pandas
        from src.gmail_service import GmailService
        from src.llm_service import LLMService
        from src.vector_store import VectorStoreService
        from src.report_generator import ReportGenerator
        print("Imports successful.")
    except ImportError as e:
        print(f"Import failed: {e}")
        sys.exit(1)

if __name__ == "__main__":
    test_imports()
    print("Verification script finished.")
