import chromadb
from langchain_chroma import Chroma
from langchain_ollama import OllamaEmbeddings
from langchain_core.documents import Document

class VectorStoreService:
    def __init__(self, collection_name="email_summaries", model="llama3"):
        self.embeddings = OllamaEmbeddings(model=model)
        self.vector_store = Chroma(
            collection_name=collection_name,
            embedding_function=self.embeddings,
            persist_directory="./chroma_db"  # Persist data locally
        )

    def add_email(self, email_data, summary, category):
        doc = Document(
            page_content=summary,  # Store summary as the main content for retrieval
            metadata={
                "email_id": email_data['id'],
                "subject": email_data['subject'],
                "sender": email_data['sender'],
                "date": email_data['date'],
                "original_body": email_data['body'][:1000], # Store truncated body to save space
                "category": category
            }
        )
        self.vector_store.add_documents([doc])
        print(f"Stored email {email_data['id']} in vector DB.")

    def query_similar_emails(self, query, k=5):
        return self.vector_store.similarity_search(query, k=k)
