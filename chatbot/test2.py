import psycopg2
from langchain_openai import OpenAI
from langchain_core.caches import BaseCache
from langchain_community.utilities import SQLDatabase
from langchain_community.agent_toolkits.sql.toolkit import SQLDatabaseToolkit
from langchain_community.agent_toolkits.sql.base import create_sql_agent
# Connect to your PostgreSQL database using your credentials
from langchain_core.messages import SystemMessage
import os
from dotenv import load_dotenv
load_dotenv()
try:
    from langchain_core.callbacks import Callbacks
except ImportError:
    class Callbacks:
        pass
class BaseCache:
    pass


api = os.getenv("OPENAI_API_KEY")
os.environ["OPENAI_API_KEY"] = api
db = SQLDatabase.from_uri(
    "postgresql://postgres:sambhav123@sambhav-db-instance.czwc8kayugg1.us-east-1.rds.amazonaws.com:5432/sambhav_db"
)

# Initialize the OpenAI language model and the SQL agent
llm = OpenAI()
SQLDatabaseToolkit.model_rebuild()
toolkit = SQLDatabaseToolkit(db=db, llm=llm)
# agent_executor = create_openai_tools_agent(llm=llm, tools=tools,prompt=SQL_PREFIX)
agent_executor = create_sql_agent(
    llm=llm,
    toolkit=toolkit,
    verbose=True
)

# Define a function to process natural language queries
def process_query(query):
    try:
        # Use the agent to process the query
        response = agent_executor.invoke({"input": query})
        return response['output']
    except Exception as e:
        return str(e)

# Example usage
if __name__ == "__main__":
    prompt = "List all sellers in DB"
    result = process_query(prompt)
    print(result)