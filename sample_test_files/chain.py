from pydantic import BaseModel
import os
from dotenv import load_dotenv
from openai import OpenAI

# Load environment variables
load_dotenv()
api_key = os.getenv("OPENAI_API_KEY")

# Initialize OpenAI client
client = OpenAI(api_key=api_key)

# Define the structured response model
class Step(BaseModel):
    image: str
    output: str

class HelpResponse(BaseModel):
    steps: list[Step]
    final_ans: str

# Sample help data
help_data = {
    "orders": [
        {
            "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/GridClusterImage5.png",
            "image_description": "Step 1 description for orders."
        },
        {
            "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/CarouselImage1.png",
            "image_description": "Step 2 description for orders."
        }
    ],
    "returns": [
        {
            "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/CarouselImage3.png",
            "image_description": "Step 1 description for returns."
        },
        {
            "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/29-04-24CertifiedThrift0158.jpg",
            "image_description": "Step 2 description for returns."
        }
    ],
    "shipping_help": [
        {
            "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/apple-touch-icon.png",
            "image_description": "Step 1 description for shipping help."
        }
    ]
}

# Determine category based on user query
def get_category_from_query(query: str) -> str:
    if "order" in query.lower():
        return "orders"
    elif "return" in query.lower():
        return "returns"
    elif "shipping" in query.lower():
        return "shipping_help"
    else:
        return None

# Generate structured response
def generate_help_response(query: str) -> HelpResponse:
    category = get_category_from_query(query)
    if category and category in help_data:
        steps = [
            Step(image=step["image_url"], output=step["image_description"])
            for step in help_data[category]
        ]
        final_ans = f"The steps for resolving the {category} issue are listed above. Follow each step carefully."
    else:
        steps = []
        final_ans = "No relevant steps found for the provided query."
    
    return HelpResponse(steps=steps, final_ans=final_ans)

# Query processing
query = "How can I create an order?"
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

help_response = response.choices[0].message.parsed

print(help_response.json())
