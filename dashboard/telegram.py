import os
import json
import logging
from typing import Dict, Optional, List
from dotenv import load_dotenv
import telebot
from pydantic import BaseModel
from langchain_core.caches import BaseCache
from langchain_openai import OpenAI
from langchain_community.agent_toolkits.sql.base import create_sql_agent
from langchain_community.utilities import SQLDatabase
from langchain_community.agent_toolkits.sql.toolkit import SQLDatabaseToolkit

class Callbacks:
    pass

class BaseCache:
    pass

# Load environment variables
load_dotenv()

# Initialize logging
logging.basicConfig(level=logging.INFO)

# Get the Telegram bot token and OpenAI API key
TELEGRAM_BOT_TOKEN = os.getenv("TELEGRAM_BOT_TOKEN")
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
POSTGRES_URI = os.getenv("POSTGRES_URL")

# Initialize the bot
bot = telebot.TeleBot(TELEGRAM_BOT_TOKEN)

# Initialize OpenAI client and set API key
os.environ["OPENAI_API_KEY"] = OPENAI_API_KEY
POSTGRES_URI = os.getenv("POSTGRES_URL")

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
    
    
with open("help_data.json", "r") as f:
    help_data = json.load(f)

# Define process_output_sql function
def process_output_sql(query, response):
    """
    Process the SQL agent output into a structured response.
    """
    formatted_query = query
    formatted_response = response["output"]
    from openai import OpenAI
    client = OpenAI(api_key=OPENAI_API_KEY)
    response = client.chat.completions.create(
        model="gpt-4o",
        messages=[
            {
                "role": "system",
                "content": (
                    f'''
                    You are a helpful assistant who formats given response into a natural language format. User should understand the response easily.
                    This was the query: {formatted_query} and response: {formatted_response}
                    Now return FORMATTED RESPONSE ONLY - NOTHING ELSE.
                    If query = "Tell me the total number of sellers"
                    RESPONSE SHOULD BE
                    {{
                        "response": "Total sellers are 3"
                    }}
                    in valid JSON.
                    '''
                )
            },
            {"role": "user", "content": formatted_response},
        ]
    )

    # Get the content from the response and parse it as JSON
    formatted_content = response.choices[0].message.content.strip()
    
    # Return as a dictionary
    return {"response": formatted_content}


# Handler for the /database command
@bot.message_handler(commands=['database'])
def handle_database(message):
    bot.reply_to(message, "Please enter your database query:")
    bot.register_next_step_handler(message, process_database_query)

def process_database_query(message):
    user_query = message.text

    try:
        # Use the agent_executor to handle the query
        response = agent_executor.invoke({"input": user_query})
        processed_response = process_output_sql(user_query, response)
        if 'response' in processed_response:
            bot.reply_to(message, processed_response['response'])
        else:
            bot.reply_to(message, f"Error: {processed_response.get('error', 'Unknown error')}")
    except Exception as e:
        logging.error(f"Error processing database query: {e}")
        bot.reply_to(message, f"An error occurred: {str(e)}")

# Define data models
class Step(BaseModel):
    point: int
    image_url: str
    image_description: str

class Category(BaseModel):
    steps: List[Step]

class HelpResponse(BaseModel):
    category: Optional[str] = None
    steps: List[Step]
    final_ans: Optional[str] = None


# # Define the process_help_query function
# def process_help_query(query: str) -> HelpResponse:
#     """
#     Process a help query using the predefined JSON file and return a structured response.
#     """
#     help_data_json = json.dumps(help_data)
#     client = OpenAI(api_key=OPENAI_API_KEY)
    
#     response = client.chat.completions.create(
#         model="gpt-4o",
#         messages=[
#             {
#                 "role": "system",
#                 "content": (
#                     f"You are a helpful assistant. Refer strictly to the given JSON data for resolving user queries.\n"
#                     f"Data: {help_data_json}\n"
#                     "Each category (orders, returns, shipping_help) contains specific steps with image URLs and descriptions.\n"
#                     "Do not generate random image URLs. Always fetch steps from the predefined data."
#                 )
#             },
#             {"role": "user", "content": query},
#         ]
#     )

#     # Get the assistant's reply
#     assistant_reply = response.choices[0].message.content

#     # Try to parse the assistant's reply into HelpResponse
#     try:
#         parsed_response = HelpResponse.model_validate_json(assistant_reply)
#         return parsed_response
#     except Exception as e:
#         logging.error(f"Failed to parse assistant's reply: {e}")
#         return None
    
# def process_help_query(query: str) -> HelpResponse:
#     """
#     Process a help query using the predefined JSON file and return a structured response.
#     """
#     api_key = os.getenv("OPENAI_API_KEY")
#     client = OpenAI(api_key=api_key)

#     # Generate response using OpenAI's beta.chat.completions.parse
#     response = client.beta.chat.completions.parse(
#         model="gpt-4o-2024-08-06",
#         messages=[
#             {
#                 "role": "system",
#                 "content": (
#                     f"You are a helpful assistant. Refer strictly to the given JSON file {help_data} "
#                     "for resolving user queries. Each category (orders, returns, shipping_help) "
#                     "contains specific steps with image URLs and descriptions. "
#                     "Do not generate random image URLs. Always fetch steps from the predefined data."
#                 )
#             },
#             {"role": "user", "content": query},
#         ],
#         response_format=HelpResponse,
#     )
#     assistant_reply = response.choices[0].message.parsed
    
#     return assistant_reply


def process_help_query(query: str) -> HelpResponse:
    """
    Process a help query using the predefined JSON file and return a structured response.
    Maintains the exact same logic as the original function.
    """
    api_key = os.getenv("OPENAI_API_KEY")
    from openai import OpenAI
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
        response_format=HelpResponse,  # Adjusted to dict instead of HelpResponse
    )

    # Parse and return the structured response
    return response.choices[0].message.parsed


def format_help_response(help_response: HelpResponse) -> str:
    """
    Format the help response into a readable Telegram message.
    """
    if not help_response or not help_response.steps:
        return "Sorry, I couldn't find any helpful information for your query."
    
    # Compile the response
    response_text = "*Help Guidance*\n\n"
    
    for step in help_response.steps:
        response_text += f"{step.point}. {step.image_description}\n"
    
    # Add final answer if available
    if help_response.final_ans:
        response_text += f"\n*Additional Information:*\n{help_response.final_ans}"
    
    return response_text

@bot.message_handler(commands=['start', 'help'])
def send_welcome(message):
    """
    Handle the /start and /help commands
    """
    welcome_message = (
        "Welcome! I'm here to help you. You can ask me questions about:\n"
        "- Orders\n"
        "- Returns\n"
        "- Shipping\n\n"
        "Just type your question, and I'll provide step-by-step guidance."
    )
    bot.reply_to(message, welcome_message)

@bot.message_handler(func=lambda message: True)
def handle_help_query(message):
    """
    Handle user help queries
    """
    try:
        # Process the query
        help_response = process_help_query(message.text)
        
        # Format the response
        formatted_response = format_help_response(help_response)
        
        # Send the response
        bot.reply_to(message, formatted_response, parse_mode='Markdown')
        
        # Send images for each step
        for step in help_response.steps:
            try:
                bot.send_photo(message.chat.id, step.image_url)
            except Exception as img_error:
                print(f"Could not send image: {img_error}")
    
    except Exception as e:
        error_message = f"Sorry, I encountered an error processing your request: {str(e)}"
        bot.reply_to(message, error_message)


# Start polling
bot.infinity_polling()