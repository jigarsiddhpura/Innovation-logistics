from fastapi import FastAPI, HTTPException, Query
from pydantic import BaseModel
import os
import json
from dotenv import load_dotenv
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel
from langchain_openai import OpenAI
from langchain_core.caches import BaseCache
from langchain_community.utilities import SQLDatabase
from langchain_community.agent_toolkits.sql.toolkit import SQLDatabaseToolkit
from langchain_community.agent_toolkits.sql.base import create_sql_agent
from utils import process_help_query
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
)

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

class QueryResponse(BaseModel):
    response: str
    
class QueryRequest(BaseModel):
    query: str    


# Combined endpoint for SQL queries
@app.post("/chat", response_model=QueryResponse)                # This is the endpoint for SQL queries
async def chat_or_help(request: QueryRequest):
    """
    Endpoint to handle SQL queries using raw JSON data.
    """
    try:
        query = request.query
        response = agent_executor.invoke({"input": query})
        return QueryResponse(response=response['output'])
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