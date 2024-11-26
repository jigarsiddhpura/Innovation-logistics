# Description: Analyze call transcript for priority using OpenAI API with GPT-4o-mini.
from openai import OpenAI
import os
from dotenv import load_dotenv
load_dotenv()


OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
client = OpenAI(api_key=OPENAI_API_KEY)

def analyze_priority(transcript: str) -> int:
    """Analyze call transcript for priority using OpenAI API with GPT-4o-mini."""
    try:
        # Create the chat completion request
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=[
                {
                    "role": "system",
                    "content": (
                        "You are an expert in evaluating the urgency of customer grievances. "
                        "Your task is to analyze the following customer call transcript and assign a priority score between 0 and 10. "
                        "A score of 0 means 'no urgency', and a score of 10 means 'extremely urgent'. "
                        "Provide the score ONLY"
                        "Return answer as 'Priority: <score>' followed by no explanation whatsover."
                    )
                },
                {
                    "role": "user",
                    "content": f"Transcript to analyze:\n{transcript}"
                }
            ],
            max_tokens=100,
            temperature=0.0  # Deterministic output
        )

        # Extract the response content
        analysis = response['choices'][0]['message']['content'].strip()
        
        # Parse the result
        if analysis.startswith("Priority:"):
            lines = analysis.split("\n")
            priority_score = int(lines[0].replace("Priority:", "").strip())
            return priority_score
        else:
            return 5  # Default priority in case of unexpected response

    except Exception as e:
        return 5  # Default priority in case of error
