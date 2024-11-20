from flask import Flask, request, jsonify
import requests
import os

app = Flask(__name__)
from dotenv import load_dotenv
load_dotenv()


# Replace with your actual Bland AI API key
BLANDAI_API_KEY = os.getenv("BLANDAI_API_KEY")
@app.route("/")
def home():
    return "Welcome to Bland AI Test!"


@app.route("/test-call", methods=["POST"])
def test_call():
    """
    Simple endpoint to test Bland AI call API.

    Example Request Body:
    {
        "phone_number": "+1234567890"
    }
    """
    data = request.json
    phone_number = data.get("phone_number")

    if not phone_number:
        return jsonify({"error": "Missing phone number"}), 400

    # Bland AI API details
    bland_api_url = "https://api.bland.ai/v1/calls"
    bland_api_headers = {
        "authorization": BLANDAI_API_KEY,  # Replace with actual API key
        "Content-Type": "application/json",
    }
    bland_api_payload = {
        "phone_number": phone_number,
        "task": """
                You are a professional grievance manager, empathetically assisting users with their issues. 
                Your role is to identify the problem and provide actionable resolutions in a polite, professional manner.
                """,
        "wait_for_greeting": False,
        "language": "ENG",
        "record": True,
    }

    try:
        # Send request to Bland AI
        response = requests.post(
            bland_api_url, json=bland_api_payload, headers=bland_api_headers
        )
        response_data = response.json()

        if response.status_code != 200:
            return jsonify({
                "error": "Failed to initiate call",
                "details": response_data
            }), 500

        return jsonify({
            "message": "Call successfully initiated",
            "response_data": response_data
        }), 200

    except Exception as e:
        return jsonify({
            "error": "An error occurred while initiating the call",
            "details": str(e)
        }), 500

if __name__ == "__main__":
    app.run(debug=True)
