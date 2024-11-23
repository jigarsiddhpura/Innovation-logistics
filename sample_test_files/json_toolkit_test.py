from fastapi import FastAPI, HTTPException
from pydantic import BaseModel
from langchain import hub
from langchain_community.agent_toolkits.json.toolkit import JsonToolkit
from langchain_community.agent_toolkits.json.base import create_json_agent
from langchain_community.tools.json.tool import JsonSpec
from langchain_openai import OpenAI
from langchain.prompts import PromptTemplate
import json
import os
from dotenv import load_dotenv
load_dotenv()

api = os.getenv("OPENAI_API_KEY")
os.environ["OPENAI_API_KEY"] = api

# FastAPI app setup
app = FastAPI()

prompt = hub.pull("hwchase17/react-chat-json")
#read json from same directory
with open('help_data.json') as f:
    help_data = json.load(f)

custom_prompt = PromptTemplate(
    template="""You are an assistant tasked with helping users navigate help documentation stored in JSON format.
The JSON contains sections such as orders, returns, and shipping help, each with step including points, image URLs, and descriptions.

When a user asks a question, identify the relevant section and return all the steps from the JSON. If no relevant data is found, inform the user.

Examples of user queries:
- "How to create an order?"
- "What are the steps for returning an item?"
- "Show me all steps for shipping help."

Respond with relevant information in JSON format. For example:
{{
    "orders": {{
        "steps": [
            {{
                "point": 1,
                "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/GridClusterImage5.png",
                "image_description": "point 1 description for orders."
            }}
        ]
    }},
    "returns": {{
        "steps": [
            {{
                "point": 1,
                "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/CarouselImage3.png",
                "image_description": "point 1 description for returns."
            }},
            {{
                "point": 2,
                "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/29-04-24CertifiedThrift0158.jpg",
                "image_description": "point 2 description for returns."
            }},
            {{
                "point": 3,
                "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/CarouselImage4.png",
                "image_description": "point 3 description for returns."
            }}
        ]
    }},
    "shipping_help": {{
        "steps": [
            {{
                "point": 1,
                "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/apple-touch-icon.png",
                "image_description": "point 1 description for shipping help."
            }},
            {{
                "point": 2,
                "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/695955_XJEEI_1030_001_100_0000_Light.avif",
                "image_description": "point 2 description for shipping help."
            }},
            {{
                "point": 3,
                "image_url": "https://aapkadhikar.s3.ap-south-1.amazonaws.com/uploads/CarouselImage1.png",
                "image_description": "Step 3 description for shipping help."
            }}
        ]
    }}
}}


Above is the json format provided to the toolkit. The toolkit will parse the JSON and return ALL STEPS OF THAT CATEGORY to the user.
ALWAYS RETURN ALL STEPS FOR THE REQUESTED SECTION, EVEN IF THE USER ASKS FOR A SPECIFIC STEP.
"""
)
# LangChain JSON toolkit setup
json_spec = JsonSpec(dict_=help_data, max_value_length=4000)
llm = OpenAI()  # Make sure to set your OpenAI API key
json_toolkit = JsonToolkit(spec=json_spec)
json_agent = create_json_agent(llm=llm, toolkit=json_toolkit, verbose=True, prompt = prompt)

# Pydantic models
class HelpQueryRequest(BaseModel):
    query: str

class HelpQueryResponse(BaseModel):
    response: dict

# # Define the endpoint
# @app.post("/help", response_model=HelpQueryResponse)
# async def query_help_guide(request: HelpQueryRequest):
#     """
#     Query help guide for a specific category and get relevant images.
#     """
#     try:
#         # Use LangChain agent to process query
#         response = json_agent.invoke({{}}"input": request.query})
#         print(request.query)
#         return HelpQueryResponse(response=response)
#     except Exception as e:
#         raise HTTPException(status_code=500, detail=str(e))


query = "I am having issues in orders"
print(query)
response = json_agent.invoke({"input": query})
print(response['output'])
