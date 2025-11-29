from langchain_ollama import OllamaLLM
from langchain_core.prompts import ChatPromptTemplate

class LLMService:
    def __init__(self, model="llama3"):
        self.llm = OllamaLLM(model=model)

    def summarize_email(self, email_content):
        prompt = ChatPromptTemplate.from_template(
            "Summarize the following email concisely:\n\n{email_content}"
        )
        chain = prompt | self.llm
        return chain.invoke({"email_content": email_content})

    def categorize_email(self, email_content):
        prompt = ChatPromptTemplate.from_template(
            "Categorize the following email into one of these categories: [Work, Personal, Promotions, Updates, Spam]. Return ONLY the category name.\n\n{email_content}"
        )
        chain = prompt | self.llm
        return chain.invoke({"email_content": email_content}).strip()
