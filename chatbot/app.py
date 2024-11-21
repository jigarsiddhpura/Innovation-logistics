import os
from dotenv import load_dotenv
from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from langchain_openai import OpenAI
from langchain_core.caches import BaseCache
from langchain_community.utilities import SQLDatabase
from langchain_community.agent_toolkits.sql.toolkit import SQLDatabaseToolkit
from langchain_community.agent_toolkits.sql.base import create_sql_agent
from fastapi.middleware.cors import CORSMiddleware

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

# Define the database connection and the LLM setup
api = os.getenv("OPENAI_API_KEY")
os.environ["OPENAI_API_KEY"] = api

db = SQLDatabase.from_uri(
    "postgresql://postgres:sambhav123@sambhav-db-instance.czwc8kayugg1.us-east-1.rds.amazonaws.com:5432/sambhav_db"
)

# Initialize the OpenAI language model and the SQL agent
llm = OpenAI()
SQLDatabaseToolkit.model_rebuild()
toolkit = SQLDatabaseToolkit(db=db, llm=llm)
agent_executor = create_sql_agent(
    llm=llm,
    toolkit=toolkit,
    verbose=True,
)

# Define request and response models for chatbot queries
class QueryRequest(BaseModel):
    query: str

class QueryResponse(BaseModel):
    response: str

# Define chatbot endpoint
@app.post("/chat", response_model=QueryResponse)
async def chatbot_endpoint(request: QueryRequest):
    """
    Endpoint to handle natural language queries and return results from the SQL agent.
    """
    try:
        query = request.query
        # Process the query using the SQL agent
        response = agent_executor.invoke({"input": query})
        return QueryResponse(response=response['output'])
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))

# Root endpoint
@app.get("/")
def read_root():
    return {"message": "Welcome to the SQL Chatbot API! Use /chat endpoint to interact."}
