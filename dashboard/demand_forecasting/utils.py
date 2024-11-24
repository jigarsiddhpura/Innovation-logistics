import os
import json
from typing import Dict, List
from dotenv import load_dotenv
from openai import OpenAI
from pydantic import BaseModel
import random 
from datetime import datetime, timedelta
from .misc import fr

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


class ForecastPoint(BaseModel):
    date: str
    inventory_level: int

# Generate realistic forecast data with ups and downs
def obtain_forecast_data(product_id: int, days: int = 30) -> List[Dict]:
    today = datetime.now()
    data = []

    if product_id in fr:
        inventory_levels = fr[product_id]
        for i, inventory_level in enumerate(inventory_levels[:days]):
            date = today + timedelta(days=i)
            data.append(ForecastPoint(date=date.strftime("%Y-%m-%d"), inventory_level=inventory_level).dict())
    else:
        # Generate random inventory levels for other product IDs
        inventory = 100  # Start with a high inventory
        for i in range(days):
            date = today + timedelta(days=i)
            change = random.randint(-15, 10)  # Random ups and downs
            inventory += change
            inventory = max(inventory, 0)  # Ensure inventory is non-negative
            data.append(ForecastPoint(date=date.strftime("%Y-%m-%d"), inventory_level=inventory).model_dump())
    
    return data

# Analyze forecast data to provide insights
def analyze_forecast(forecast_data: List[ForecastPoint]) -> Dict:
    """
    Analyze forecast data to check for low inventory alerts and reorder recommendations.
    """
    low_inventory_threshold = 10  # Threshold for low inventory alert
    low_inventory_alert = any(point.inventory_level < low_inventory_threshold for point in forecast_data)
    recommended_reorder_quantity = None

    if low_inventory_alert:
        # Calculate the average inventory level and suggest reorder quantity
        average_inventory = sum(point.inventory_level for point in forecast_data) // len(forecast_data)
        recommended_reorder_quantity = min(100, average_inventory)  # Example reorder logic

    return {
        "low_inventory_alert": low_inventory_alert,
        "recommended_reorder_quantity": recommended_reorder_quantity
    }