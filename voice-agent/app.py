from flask import Flask, request, jsonify
from pymongo import MongoClient
from datetime import datetime
import requests
import os
from dotenv import load_dotenv
from openai_priority import analyze_priority
load_dotenv()

app = Flask(__name__)
OPENAI_API_KEY = os.getenv("OPENAI_API_KEY")
BLANDAI_API_KEY = os.getenv("BLANDAI_API_KEY")
MONGO_URI = "mongodb+srv://aryannvr:aryan181527@cluster0.pgxdqvd.mongodb.net/?retryWrites=true&w=majority&appName=Cluster0"

# MongoDB Configuration
try:
    client = MongoClient(MONGO_URI, serverSelectionTimeoutMS=5000)  # 5 seconds timeout
    db = client["grievance_calls"]
    calls_collection = db["calls"]
    print("MongoDB connected successfully")
except Exception as e:
    print(f"Failed to connect to MongoDB: {e}")

@app.route("/request-call", methods=["POST"])
def request_call():
    """
    Endpoint for Shopify sellers to request a grievance call.

    
    {
    "user_id": "12345",
    "phone_number": "+1234567890"
    }

    """
    data = request.json
    user_id = data.get("user_id")
    phone_number = data.get("phone_number")

    if not (user_id and phone_number):
        return jsonify({"error": "Missing required fields"}), 400

    # Bland AI API details
    bland_api_url = "https://api.bland.ai/v1/calls"
    bland_api_headers = {
        "authorization": BLANDAI_API_KEY,  # Replace with actual token
        "Content-Type": "application/json",
    }
    bland_api_payload = {
        "phone_number": phone_number,
        "task": """
                You are a professional and empathetic grievance manager for Amazon Multi-Channel Fulfillment (MCF), assisting Shopify users with their order-related issues. Your role is to listen attentively, identify the grievance, and provide actionable resolutions or next steps in a polite and clear manner. 
                Maintain a professional tone, show understanding of the user's frustration, and focus on resolving the issue promptly. 
                If necessary, escalate the matter to the appropriate team while reassuring the user of a swift resolution.
                Ensure all interactions reflect Amazon's commitment to excellent service and customer satisfaction.
                """,
        "wait_for_greeting": False,
        "language": "ENG",
        "record": True,
    }

    try:
        response = requests.post(
            bland_api_url, json=bland_api_payload, headers=bland_api_headers
        )
        response_data = response.json()
        
        if response.status_code != 200:
            return (
                jsonify({"error": "Failed to initiate call", "details": response_data}),
                500,
            )

        # Extract details from response
        call_id = response_data.get("call_id")
        status = response_data.get("status")

        # Save call metadata in MongoDB
        call_data = {
            "call_id": call_id,
            "user_id": user_id,
            "phone_number": phone_number,
            "status": status,
            "timestamp": datetime.now(),
            "duration": None,
            "transcript_summary": None,
            "priority": None,
        }
        calls_collection.insert_one(call_data)
        print(f"Call data inserted for call_id: {call_id}")

        return jsonify({"message": "Call successfully queued", "call_id": call_id}), 200

    except Exception as e:
        print(f"Error in request_call: {e}")
        return (
            jsonify(
                {"error": "An error occurred while sending the call", "details": str(e)}
            ),
            500,
        )

@app.route('/call-details/<call_id>', methods=['GET'])
def call_details(call_id):
    """
    Endpoint to fetch call details from Bland AI.
    """
    bland_api_url = f"https://api.bland.ai/v1/calls/{call_id}"
    bland_api_headers = {
        "authorization": BLANDAI_API_KEY  # Replace with actual token
    }

    try:
        response = requests.get(bland_api_url, headers=bland_api_headers)
        response_data = response.json()

        if response.status_code != 200:
            return jsonify({"error": "Failed to fetch call details", "details": response_data}), 500

        # Extract necessary details
        call_length = response_data.get("call_length")
        city = response_data["variables"].get("city")
        state = response_data["variables"].get("state")
        summary = response_data.get("summary", "")
        concatenated_transcript = response_data.get("concatenated_transcript", "")

        # Calculate grievance priority using OpenAI API
        prompt = f"Assign a priority (0-10) to the following grievance: {summary}"
        priority = analyze_priority(prompt)

        # Update MongoDB
        calls_collection.update_one(
            {"call_id": call_id},
            {"$set": {
                "duration": call_length,
                "state": state,
                "city": city,
                "transcript_summary": summary,
                "priority": priority
            }}
        )
        print(f"Call details updated for call_id: {call_id}")

        return jsonify({
            "call_id": call_id,
            "duration": call_length,
            "state": state,
            "city": city,
            "transcript_summary": summary,
            "priority": priority,
            "transcript": concatenated_transcript
        }), 200

    except Exception as e:
        print(f"Error in call_details: {e}")
        return jsonify({"error": "An error occurred while fetching call details", "details": str(e)}), 500

if __name__ == "__main__":
    app.run(port=5000)