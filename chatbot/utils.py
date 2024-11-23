import os
import json
from dotenv import load_dotenv
from openai import OpenAI
from pydantic import BaseModel

# Load environment variables
load_dotenv()

# Load help data from JSON file
with open("help_data.json") as f:
    help_data = json.load(f)

# Define structured response models
class Step(BaseModel):
    image: str
    output: str

class HelpResponse(BaseModel):
    steps: list[Step]
    final_ans: str

# Function to process help queries
def process_help_query(query: str) -> HelpResponse:
    """
    Process a help query using the predefined JSON file and return a structured response.
    """
    api_key = os.getenv("OPENAI_API_KEY")
    client = OpenAI(api_key=api_key)

    # Generate response using OpenAI's beta.chat.completions.parse
    response = client.beta.chat.completions.parse(
        model="gpt-4o-2024-08-06",
        messages=[
            {
                "role": "system",
                "content": (
                    f"You are a helpful assistant. Refer strictly to the given JSON file {help_data} "
                    "for resolving user queries. Each category (orders, returns, shipping_help) "
                    "contains specific steps with image URLs and descriptions. "
                    "Do not generate random image URLs. Always fetch steps from the predefined data."
                )
            },
            {"role": "user", "content": query},
        ],
        response_format=HelpResponse,
    )

    # Parse and return the structured response
    return response.choices[0].message.parsed
