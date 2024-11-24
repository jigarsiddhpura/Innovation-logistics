from fastapi import FastAPI, HTTPException, Query
from fastapi.responses import JSONResponse
from pydantic import BaseModel
import os
import json
from dotenv import load_dotenv
import requests
from fastapi.middleware.cors import CORSMiddleware
from langchain_openai import OpenAI
from langchain_core.caches import BaseCache
from langchain_community.utilities import SQLDatabase
from langchain_community.agent_toolkits.sql.toolkit import SQLDatabaseToolkit
from langchain_community.agent_toolkits.sql.base import create_sql_agent
from demand_forecasting.utils import process_help_query, obtain_forecast_data, analyze_forecast
import logging

logging.basicConfig(level=logging.INFO)# for logging

# Load environment variables
load_dotenv()

class Callbacks:
    pass

class BaseCache:
    pass

# Initialize FastAPI app
app = FastAPI()

# Allow Cross-Origin Resource Sharing (CORS) for frontend communication
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # Update to specify allowed origins
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

sagemaker_endpoint = os.getenv("SAGEMAKER_ENDPOINT")
# Initialize OpenAI client and SQL database
api_key = os.getenv("OPENAI_API_KEY")
POSTGRES_URI = os.getenv("POSTGRES_URL")
os.environ["OPENAI_API_KEY"] = api_key

llm = OpenAI()
db = SQLDatabase.from_uri(database_uri=POSTGRES_URI)
toolkit = SQLDatabaseToolkit(db=db, llm=llm)
agent_executor = create_sql_agent(
    llm=llm,
    toolkit=toolkit,
    verbose=True,
    top_k=5,
    agent_executor_kwargs={
        "handle_parsing_errors": True  # Enable retrying on parsing errors
    }
    
)

def process_output_sql(query,response):   # Process SQL agent output into a well structured response
    """
    Process the SQL agent output into a structured response.
    """
    from openai import OpenAI
    client = OpenAI()
    formatted_query = query
    formatted_response = response["output"]
    
    response = client.chat.completions.create(
    model="gpt-4o",
    response_format={"type": "json_object"},
    messages=[
            {
                "role": "system",
                "content": (
                    f'''
                    You are a helpful assistant who formats given response into a natural language format. User should understand the response easily. 
                    This was the query: {formatted_query} and response : {formatted_response} 
                    Now return FORMATTEDD RESPONSE ONLY - NOTHING ELSE 
                    if query = "Tell me the total number of sellers" 
                    RESPONSE SHOULD BE 
                    {{
                        "response": "Total sellers are 3"
                    }}
                    in valid JSON
                    '''
                )
            },
            {"role": "user", "content": formatted_response},
        ]
    )
    
    # Get the content from the response and parse it as JSON
    try:
        json_response = json.loads(response.choices[0].message.content)
        return json_response  # Returns a Python dictionary
    except json.JSONDecodeError as e:
        return {"error": f"Failed to parse JSON response: {str(e)}"}
    
    



with open("help_data.json") as f:
    help_data = json.load(f)

# Define structured response models
class Step(BaseModel):
    image: str
    output: str

class HelpResponse(BaseModel):
    steps: list[Step]
    final_ans: str

class QueryResponse(BaseModel):
    response: str
    
class QueryRequest(BaseModel):
    query: str    
class ForecastPoint(BaseModel):
    date: str
    inventory_level: int

class ForecastResponse(BaseModel):
    product_id: int
    forecast: list[ForecastPoint]
    low_inventory_alert: bool
    recommended_reorder_quantity: int
        
class ForecastRequest(BaseModel):
    product_id: int
    days: int = 30

# Combined endpoint for SQL queries
@app.post("/chat", response_model=QueryResponse)                # This is the endpoint for SQL queries
async def chat_or_help(request: QueryRequest):
    """
    Endpoint to handle SQL queries using raw JSON data.
    """
    try:
        query = request.query
        response = agent_executor.invoke({"input": query})
        processed_response = process_output_sql(query,response)
        return processed_response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# Endpoint for Help queries
@app.post("/help", response_model=HelpResponse)               # This is the endpoint for help queries ( JSON DATA QUERY TO RETURN ALL STEPS OF THAT CATEGORY)
async def help_endpoint(request: QueryRequest):
    """
    Endpoint to handle help queries using raw JSON data.
    """
    try:
        query = request.query
        response = process_help_query(query)
        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))




@app.post("/api/get-forecast", response_model=dict)
async def get_forecast(request: ForecastRequest):
    """
    Fetch forecasted inventory levels for a product.
    """
    try:
        # Connect to the SageMaker endpoint
        payload = {"product_id": request.product_id, "days": request.days}
        response = requests.post(f"https://runtime.sagemaker.amazonaws.com/endpoints/{sagemaker_endpoint}/invocations", json=payload)
        response.raise_for_status()
        return response.json()
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/api/forecast", response_model=ForecastResponse)
async def get_forecast_data(productid: int = Query(..., description="The product ID to forecast inventory for")):
    """
    Endpoint to get inventory forecast for a given product ID.
    """
    try:
        # Generate forecast data
        forecast_data = obtain_forecast_data(product_id=productid, days=30)
        analysis = analyze_forecast([ForecastPoint(**data) for data in forecast_data])

        # Prepare the response
        response = ForecastResponse(
            product_id=productid,
            forecast=forecast_data,
            low_inventory_alert=analysis["low_inventory_alert"],
            recommended_reorder_quantity=analysis["recommended_reorder_quantity"]
        )
        return response
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))